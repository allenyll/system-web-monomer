package com.allenyll.sw.sso.config;

import com.allenyll.sw.sso.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    
    @Resource
    AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册TestInterceptor拦截器
        InterceptorRegistration registration = registry.addInterceptor(authInterceptor);
        registration.addPathPatterns("/**");
        registration.excludePathPatterns(                         //添加不拦截路径
                "/auth/*", "/loginTest",
                "/login.html","/assets/**",
                "/css/**","/data/**","/fonts/**",
                "/img/**","/js/**","/crypto/**","/assembly/**",
                "/layui/**", "/nacos/**",
                "/wx/**"
        );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }
}
