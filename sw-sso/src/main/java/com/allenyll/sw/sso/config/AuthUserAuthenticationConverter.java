package com.allenyll.sw.sso.config;

import com.allenyll.sw.common.entity.auth.AuthUser;
import com.allenyll.sw.sso.service.impl.UserDetailsServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description:
 * @Author:       allenyll
 * @Date:         2020/8/20 8:02 下午
 * @Version:      1.0
 */
@Component
public class AuthUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    @Resource
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        LinkedHashMap<String, Object> res = new LinkedHashMap<>();
        String username = authentication.getName();
        res.put("username", username);
        Object principal = authentication.getPrincipal();
        AuthUser user = null;
        if (principal instanceof AuthUser) {
            user = (AuthUser) principal;
        } else {
            // refresh_token 默认不去调用userDetailsService获取用户信息，手动获取。
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            AuthUser authUser = (AuthUser) userDetails;
        }

        if (user != null) {
            res.put("username", user.getUsername());
            res.put("id", user.getId());
            if (user.getAuthorities() != null && !CollectionUtils.isEmpty(user.getAuthorities())) {
                res.put("authorities", user.getAuthorities());
            }
        }

        return res;
    }
}
