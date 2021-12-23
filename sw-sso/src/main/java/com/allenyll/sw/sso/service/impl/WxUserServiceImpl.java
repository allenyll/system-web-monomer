package com.allenyll.sw.sso.service.impl;

import com.allenyll.sw.common.entity.auth.AuthToken;
import com.allenyll.sw.common.entity.auth.AuthUser;
import com.allenyll.sw.common.entity.customer.Customer;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.entity.wechat.WxCodeResponse;
import com.allenyll.sw.common.util.DateUtil;
import com.allenyll.sw.common.util.Result;
import com.allenyll.sw.common.util.SnowflakeIdWorker;
import com.allenyll.sw.core.cache.util.CacheUtil;
import com.allenyll.sw.sso.factory.AuthUserFactory;
import com.allenyll.sw.sso.properties.WeChatProperties;
import com.allenyll.sw.sso.service.IWxUserService;
import com.allenyll.sw.sso.util.AuthConstants;
import com.allenyll.sw.system.mapper.member.CustomerMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author yu.leilei
 * @since 2018-10-22
 */
@Slf4j
@Service("wxUserService")
public class WxUserServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements IWxUserService {
    
    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    WxUserDetailsServiceImpl wxUserDetailsService;

    @Autowired
    private CacheUtil cacheUtil;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    WeChatProperties weChatProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${auth.wx.appId}")
    private String clientId;

    @Value("${auth.wx.appSecret}")
    private String clientSecret;

    @Value("${auth.ttl}")
    private long ttl;

    private static final Set<String> SCOPE;

    static {
		SCOPE = Sets.newHashSet();
		SCOPE.add("read");
		SCOPE.add("write");
    }

    @Override
    public AuthToken token(HttpServletRequest request, String code, String mode) {
        AuthToken authToken = new AuthToken();
        UserDetails userDetails = null;
        try {
            userDetails = loadUserByJsCode(code, mode, authToken);
        } catch (UsernameNotFoundException e) {
            log.error("小程序登录异常：{}", e.getMessage());
            return null;
        }
        String username = userDetails.getUsername();
        String password = passwordEncoder.encode(username);
        authService.login(request, authToken, username, password, clientId, clientSecret);
        return authToken;
    }

    private UserDetails loadUserByJsCode(String code, String mode, AuthToken authToken) throws UsernameNotFoundException {
        WxCodeResponse wxCodeResponse = getWxCodeSession(code, mode);
        String openid = wxCodeResponse.getOpenid();
        authToken.setOpenid(openid);
        cacheUtil.set(AuthConstants.WX_CURRENT_OPENID + "_" + mode + "_" + openid, openid, ttl);
        if (StringUtils.isEmpty(openid)) {
            log.error("登陆出错:无openid信息, 返回结果: {}", wxCodeResponse);
            throw new UsernameNotFoundException("登录出错，未查询到openid信息");
        }

        // 根据openId查询是否存在用户
        Result<Customer> result = this.queryUserByOpenId(openid, mode);
        if (!result.isSuccess()) {
            log.error("根据openId查询是否存在用户失败, 返回结果: {}", result);
            throw new UsernameNotFoundException("登录出错，根据openId查询是否存在用户失败");
        }
        Customer customer = result.getData();
        Long customerId;
        if (customer == null) {
            customer = new Customer();
            String name = UUID.randomUUID().toString().replace("-", "");
            customer.setCustomerName(name);
            //用户名MD5然后再加密作为密码
            customer.setPassword(passwordEncoder.encode(name));
            customer.setOpenid(openid);
            customer.setStatus("SW0001");
            customer.setIsDelete(0);
            customer.setAddTime(DateUtil.getCurrentDateTime());
            customerId = SnowflakeIdWorker.generateId();
            customerMapper.insert(customer);
        } else {
            customerId = customer.getId();
        }

        // cacheUtil.set(AuthConstants.WX_SESSION_KEY + "_" + openid, wxCodeResponse.getSessionKey());
        User user = new User();
        user.setId(customerId);
        user.setUserName(customer.getCustomerName());
        user.setAccount(String.valueOf(customerId));
        user.setPassword(customer.getPassword());
        List<GrantedAuthority> authorities = new ArrayList<>();
        AuthUser authUser = AuthUserFactory.create(user, authorities);
        return authUser;
    }

    private WxCodeResponse getWxCodeSession(String code, String mode) {

        String urlString = "?appid={appid}&secret={secret}&js_code={code}&grant_type={grantType}";
        Map<String, Object> map = new HashMap<>();
        String appId = weChatProperties.getAppId();
        String secret = weChatProperties.getAppSecret();
        if ("sweb".equals(mode)) {
            appId = weChatProperties.getAppId2();
            secret = weChatProperties.getAppSecret2();
        }
        map.put("appid", appId);
        map.put("secret", secret);
        map.put("code", code);
        map.put("grantType", weChatProperties.getGrantType());
        String response = restTemplate.getForObject(weChatProperties.getSessionHost() + urlString, String.class, map);

        ObjectMapper objectMapper = new ObjectMapper();
        WxCodeResponse wxCodeResponse;
        try {
            wxCodeResponse = objectMapper.readValue(response, WxCodeResponse.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            wxCodeResponse = null;
            e.printStackTrace();
        }

        log.info(wxCodeResponse.toString());
        if (null == wxCodeResponse) {
            throw new RuntimeException("调用微信接口失败");
        }
        if (wxCodeResponse.getErrcode() != null) {
            throw new RuntimeException(wxCodeResponse.getErrMsg());
        }

        return wxCodeResponse;
    }

    @Override
    public Result<Customer> queryUserByOpenId(String openid, String mode) {
        Result<Customer> result = new Result<>();
        String currentOpenId = cacheUtil.get(AuthConstants.WX_CURRENT_OPENID + "_" + mode + "_" + openid, String.class);
        QueryWrapper<Customer> wrapper = new QueryWrapper<>();
        if (openid.equals(currentOpenId)) {
            wrapper.eq("openid", openid);
            Customer customer = customerMapper.selectOne(wrapper);
            result.setData(customer);
        } else {
            result.fail("当前登录用户不正确");
        }
        return result;
    }

    @Override
    public Customer selectUserByName(String username) {
        QueryWrapper<Customer> wrapper = new QueryWrapper<>();
        wrapper.eq("customer_name", username);
        Customer customer = customerMapper.selectOne(wrapper);
        return customer;
    }

}
