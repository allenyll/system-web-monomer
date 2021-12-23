package com.allenyll.sw.sso.service.impl;

import com.allenyll.sw.common.entity.customer.Customer;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.core.cache.util.CacheUtil;
import com.allenyll.sw.common.entity.auth.AuthUser;
import com.allenyll.sw.sso.factory.AuthUserFactory;
import com.allenyll.sw.sso.properties.WeChatProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:  微信用户
 * @Author:       allenyll
 * @Date:         2020/9/11 4:01 下午
 * @Version:      1.0
 */
@Slf4j
@Service("wxUserDetailsService")
public class WxUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    WeChatProperties weChatProperties;

    @Autowired
    private WxUserServiceImpl wxUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    ClientDetailsService clientDetailsService;

    @Autowired
    CacheUtil cacheUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //取出身份，如果身份为空说明没有认证
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //没有认证统一采用HTTP_BASIC认证，HTTP_BASIC中存储了CLIENT_ID和CLIENT_SECRET，开始认证client_id和client_secret
        if(authentication == null){
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
            if(clientDetails!=null){
                //秘钥
                String clientSecret = clientDetails.getClientSecret();
                //静态方式
                //return new User(username,new BCryptPasswordEncoder().encode(clientSecret), AuthorityUtils.commaSeparatedStringToAuthorityList(""));
                //数据库查找方式
                return new org.springframework.security.core.userdetails.User(username, clientSecret, AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            }
        }

        Customer customer = wxUserService.getById(username);
        if (customer == null) {
            log.error("用户{}不存在", username);
            throw new UsernameNotFoundException(String.format("No customer found with username '%s'.", username));
        }
        User user = new User();
        user.setId(customer.getId());
        user.setUserName(customer.getCustomerName());
        user.setAccount(String.valueOf(customer.getId()));
        user.setPassword(customer.getPassword());
        List<GrantedAuthority> authorities = new ArrayList<>();
        AuthUser authUser = AuthUserFactory.create(user, authorities);
        log.info("登录成功！用户: {}", authUser.getUsername());
        return authUser;
    }


}
