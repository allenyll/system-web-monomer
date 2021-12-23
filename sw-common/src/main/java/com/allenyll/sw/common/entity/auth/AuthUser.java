package com.allenyll.sw.common.entity.auth;

import com.allenyll.sw.common.entity.system.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

/**
 * 实现UserDetails接口：
 * 这个接口中规定了用户的几个必须要有的方法，
 * 所以我们创建一个JwtUser类来实现这个接口。
 * 为什么不直接使用User类？因为这个UserDetails
 * 完全是为了安全服务的，它和我们的领域类可能有
 * 部分属性重叠，但很多的接口其实是安全定制的，
 * 所以最好新建一个类
 *
 * 自定义用户，用于用户认证 ，为了安全服务的User
 *
 * @Author: allenyll
 * @Date: 下午 4:02 2018/5/24 0024
 */
public class AuthUser implements UserDetails {

    private final long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Date lastPasswordResetDate;

    private User user;

    public AuthUser(
            long id,
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            Date lastPasswordResetDate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.lastPasswordResetDate = lastPasswordResetDate;
        this.user = new User();
        this.user.setId(id);
        this.user.setAccount(username);
        this.user.setPassword(password);
    }

    /**
     * 返回分配给用户的角色列表
     * @return 用户的角色列表
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 账户是否未过期
     * @return 是否未过期
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户是否未锁定
     * @return 是否未锁定
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 密码是否未过期
     * @return 是否未过期
     */
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账户是否激活
     * @return 是否激活
     */
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 这个是自定义的，返回上次密码重置日期
     * @return 上次密码重置日期
     */
    @JsonIgnore
    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

}
