package com.allenyll.sw.sso.config;

import com.allenyll.sw.sso.constants.IgnoreUrl;
import com.allenyll.sw.sso.provider.MyAuthenticationManager;
import com.allenyll.sw.sso.provider.WxDaoAuthenticationProvider;
import com.allenyll.sw.sso.service.impl.UserDetailsServiceImpl;
import com.allenyll.sw.sso.service.impl.WxUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

import javax.annotation.Resource;

/**
 * @Description:
 * @Author:       allenyll
 * @Date:         2020/8/20 2:18 下午
 * @Version:      1.0
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${auth.common.clientId}")
    private String clientId;

    @Value("${auth.common.clientSecret}")
    private String clientSecret;

    @Value("${auth.wx.appId}")
    private String appId;

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    WxUserDetailsServiceImpl wxUserDetailsService;

    @Resource
    private JwtDecoder jwtDecoder;

    /***
     * 忽略安全拦截的URL
     * @param web WebSecurity
     * @throws Exception 异常
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(IgnoreUrl.AUTH_WHITELIST);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return myAuthenticationManager();
    }

    public MyAuthenticationManager myAuthenticationManager() {
        JwtAuthenticationProvider jwtAuthenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
        MyAuthenticationManager myAuthenticationManager = new MyAuthenticationManager(jwtAuthenticationProvider);
        myAuthenticationManager.setProvider(appId, new WxDaoAuthenticationProvider(wxUserDetailsService));
        myAuthenticationManager.setProvider(clientId, commonDaoAuthenticationProvider());
        return myAuthenticationManager;
    }

    public AuthenticationProvider commonDaoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsServiceImpl);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                //.httpBasic()        //启用Http基本身份验证
                //.and()
                .formLogin()       //启用表单身份验证
                .and()
                .authorizeRequests()    //限制基于Request请求访问
                .antMatchers("/user").hasRole("admin")
                .antMatchers("/auth/**", "/logout/**", "/login", "/wx/auth/token", "/wx/home/index")
                .permitAll()
                .anyRequest().permitAll();
                //.authenticated();       //其他请求都需要经过验证

        //开启表单登录
        //设置访问登录页面的路径
        // http.formLogin().loginPage("/auth/loginPage, /wx/auth/token");
    }
}
