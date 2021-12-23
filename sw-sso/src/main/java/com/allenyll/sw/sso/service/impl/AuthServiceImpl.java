package com.allenyll.sw.sso.service.impl;

import com.allenyll.sw.common.util.JwtUtil;
import com.allenyll.sw.common.util.StringUtil;
import com.allenyll.sw.core.cache.util.CacheUtil;
import com.allenyll.sw.common.entity.auth.AuthToken;
import com.allenyll.sw.sso.exception.TokenException;
import com.allenyll.sw.sso.service.IAuthService;
import com.allenyll.sw.sso.util.*;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:  授权登录业务
 * @Author:       allenyll
 * @Date:         2020/8/24 11:19 上午
 * @Version:      1.0
 */
@Slf4j
@Service("authService")
public class AuthServiceImpl implements IAuthService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CacheUtil cacheUtil;

    @Value("${auth.ttl}")
    private long ttl;
    
    @Override
    public AuthToken login(HttpServletRequest request, AuthToken authToken, String username, String password, String clientId, String clientSecret) {
        String uri = request.getRequestURL().toString();
        Pattern pattern = Pattern.compile("/");
        Matcher findMatcher = pattern.matcher(uri);
        int number = 0;
        while(findMatcher.find()) {
            number++;
            if(number == 3){ 
                break;
            }
        }
        int index = findMatcher.start(); 
        uri = uri.substring(0, index+1);
        String url = uri + "/oauth/token";

        // 使用密码模式获取令牌
        MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.put("username", Collections.singletonList(username));
        bodyMap.put("password", Collections.singletonList(password));
        bodyMap.put("grant_type", Collections.singletonList("password"));
        bodyMap.put("scope", Collections.singletonList("all"));
        bodyMap.put("client_id", Collections.singletonList(clientId));
        bodyMap.put("client_secret", Collections.singletonList(clientSecret));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "multipart/form-data");
        // headers.set("authorization", AuthUtil.getHttpBasic(clientId, clientSecret));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(bodyMap, headers);

        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                super.handleError(response);
            }
        });

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        Map data = response.getBody();
        // log.info("授权返回令牌信息：{}", data);

        if (CollectionUtils.isEmpty(data) || data.get(AuthConstants.ACCESS_TOKEN) == null
                || data.get(AuthConstants.REFRESH_TOKEN) == null || data.get(AuthConstants.JTI) == null) {
            //申请令牌失败
            throw new RuntimeException("申请令牌失败");
        }

        authToken.setAccessToken((String) data.get(AuthConstants.ACCESS_TOKEN));
        authToken.setRefreshToken((String) data.get(AuthConstants.REFRESH_TOKEN));
        authToken.setJti((String) data.get(AuthConstants.JTI));

        cacheUtil.set(authToken.getJti(), authToken.getAccessToken(), ttl);

        return authToken;
    }

    @Override
    public Map<String, String> getAuthStatus(HttpServletRequest request, String target, HttpServletResponse response) throws TokenException {
        // 从Cookie中获取jti
        Map<String, String> cookieMap = CookieUtil.getCookie(request, "uid");
        if (MapUtils.isEmpty(cookieMap)) {
            return null;
        }
        String uid = cookieMap.get("uid");
        if (StringUtil.isEmpty(uid)) {
            return null;
        }

        // 根据uid从redis中获取token
        String authToken = cacheUtil.get(uid, String.class);
        try {
            // 如果令牌存在,解析jwt令牌,判断该令牌是否合法,如果令牌不合法,则向客户端返回错误提示信息
            Claims claims = JwtUtil.verifyToken(authToken);
            if (claims == null) {
                return null;
            }
        } catch (Exception e) {
            throw new TokenException("令牌解析失败");
        }

        Map<String, String> resultMap = new HashMap<>(5);
        resultMap.put("authToken", authToken);
        resultMap.put("uid", uid);
        
        return resultMap;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 从Cookie中获取jti
        Map<String, String> cookieMap = CookieUtil.getCookie(request, "uid");
        String uid = "";
        if (!CollectionUtils.isEmpty(cookieMap)) {
            uid = cookieMap.get("uid");
        }
        if (StringUtil.isNotEmpty(uid)) {
            cacheUtil.remove(uid);
        }
    }

}
