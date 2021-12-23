package com.allenyll.sw.sso.interceptor;

import com.allenyll.sw.common.constants.BaseConstants;
import com.allenyll.sw.common.constants.SecurityConstants;
import com.allenyll.sw.common.entity.customer.Customer;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.JwtUtil;
import com.allenyll.sw.common.util.SpringContextHolder;
import com.allenyll.sw.common.util.StringUtil;
import com.allenyll.sw.core.cache.util.CacheUtil;
import com.allenyll.sw.system.service.member.ICustomerService;
import com.allenyll.sw.system.base.IUserService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description:  登录认证拦截器
 * @Author:       allenyll
 * @Date:         2021/8/24 下午7:08
 * @Version:      1.0
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        CacheUtil cacheUtil = SpringContextHolder.getBean("cacheUtil");
        IUserService userService = SpringContextHolder.getBean("userService");
        ICustomerService customerService = SpringContextHolder.getBean("customerService");
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return false;
        }
        String path = request.getRequestURI();
        if (path.indexOf("api-docs") != -1) {
            return false;
        }
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String loginType = request.getHeader(BaseConstants.LOGIN_TYPE);

        if (StringUtil.isEmpty(header)) {
            log.info("请求认证消息不能为空！");
            response.setStatus(401);
            response.getWriter().write( "请求认证消息不能为空!" );
            return false;
        }

        Claims claims;
        try {
            claims = JwtUtil.verifyToken(header);
        } catch (Exception e) {
            log.info("获取Claims失败, token 过期！");
            response.getWriter().write( "获取Claims失败, token 过期!" );
            return false;
        }
        if (claims != null) {
            String username = (String) claims.get("username");
            String jti = (String) claims.get("jti");
            // 查看缓存是否失效
            String redisToken = cacheUtil.get(jti, String.class);
            if (StringUtil.isEmpty(redisToken)) {
                log.info("token缓存过期！");
                response.getWriter().write( "获取Claims失败, token 过期!" );
                return false;
            }
            if (StringUtil.isNotEmpty(username)) {
                User user;
                request.setAttribute(SecurityConstants.USER_HEADER, username);
                if (BaseConstants.SW_WECHAT.equals(loginType)) {
                    Customer customer = customerService.getById(username);
                    user = new User();
                    user.setId(customer.getId());
                    user.setUserName(customer.getCustomerName());
                    user.setAccount(customer.getCustomerAccount());
                } else {
                    user = userService.getById(username);
                }
                request.setAttribute(SecurityConstants.CURRENT_USER, user);
                return true;
            }
            
        }
        
        return false;
    }
}
