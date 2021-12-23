package com.allenyll.sw.sso.provider;

import com.google.common.collect.Maps;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

import java.util.Map;

/**
 * @Description:  自定义AuthenticationManager, 可根据不同的client_id来做不同的处理
 * @Author:       allenyll
 * @Date:         2020/9/11 4:25 下午
 * @Version:      1.0
 */
public class MyAuthenticationManager implements AuthenticationManager {

    private static final String CLIENT_ID = "client_id";

    private Map<String, AuthenticationProvider> providerMap = Maps.newHashMap();

    private JwtAuthenticationProvider jwtAuthenticationProvider;

    public MyAuthenticationManager(JwtAuthenticationProvider jwtAuthenticationProvider){
        super();
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
    }

    public MyAuthenticationManager setProvider(String clientId, AuthenticationProvider provider) {
        providerMap.put(clientId, provider);
        return this;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Class<? extends Authentication> authenticationClass =  authentication.getClass();
        if (jwtAuthenticationProvider.supports(authenticationClass)) {
            Authentication auth = jwtAuthenticationProvider.authenticate(authentication);
            return auth;
        } else {
            Map<String, Object> detailsMap = (Map<String, Object>) authentication.getDetails();
            Object clientId = detailsMap.get(CLIENT_ID);
            if (clientId == null) {
                throw new IllegalArgumentException("client_id is not found");
            }
            AuthenticationProvider provider = providerMap.get(clientId);
            if (provider == null) {
                throw new BadCredentialsException("invalid client_id: AuthenticationProvider is not queried based on client_id");
            }
            Authentication auth = provider.authenticate(authentication);
            return auth;
        }
    }
}
