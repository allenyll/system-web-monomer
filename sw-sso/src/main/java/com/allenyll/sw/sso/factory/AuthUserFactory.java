package com.allenyll.sw.sso.factory;

import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.entity.auth.AuthUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 创建自定义AuthUser工厂，用户根据用户创建自定义AuthUser
 * @Author: yu.leilei
 * @Date: 下午 4:31 2018/5/24 0024
 */
public class AuthUserFactory {

    public AuthUserFactory() {
    }

    /**
     * 根据系统用户创建子订单jwtUser
     * @param user 用户
     * @param authorities 权限
     * @return authUser
     */
    public static AuthUser create(User user, List<GrantedAuthority> authorities) {
        return new AuthUser(
                user.getId(),
                user.getAccount(),
                user.getPassword(),
                authorities,
                user.getLastPasswordResetDate()
        );
    }

    public static List<GrantedAuthority> mapToGrantedAuthorities(List<Map<String, String>> authorities) {
        return authorities.stream().map(map -> {
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(map.get("permission"));
            return authority;
        }).collect(Collectors.toList());
    }
}
