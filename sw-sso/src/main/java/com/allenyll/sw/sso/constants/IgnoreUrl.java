package com.allenyll.sw.sso.constants;

/**
 * @author yuleilei
 */
public class IgnoreUrl {

    /**
     *  swagger ui忽略
     */
    public static final String[] AUTH_WHITELIST = {
            // 静态资源
            "/login.html","/assets/**",
            "/css/**","/data/**","/fonts/**",
            "/img/**","/js/**","/crypto/**",
            "/layui/**",
            "/favicon.ico",
            "/assembly/**",
            // 登录相关
            "/nacos/**",
            "/", "/auth/login", "/loginTest",
            "/error",
            "/auth/logout","/auth/loginPage",
            // 小小程序
            "/wx/**",
            // -- swagger ui
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v2/api-docs",
            "/webjars/**",
            // swagger-boostrap-ui
            "/doc.html"
    };
}
