package com.allenyll.sw.sso.service;

import com.allenyll.sw.common.entity.auth.AuthToken;
import com.allenyll.sw.sso.exception.TokenException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Description:
 * @Author:       allenyll
 * @Date:         2020/8/20 4:02 下午
 * @Version:      1.0
 */
public interface IAuthService {

    /**
     * 登录
     *
     * @param request
     * @param username 用户名
     * @param password 密码
     * @param clientId 客户端
     * @param clientSecret 客户端密码
     * @return 登录信息
     */
    AuthToken login(HttpServletRequest request, AuthToken authToken, String username, String password, String clientId, String clientSecret);

    /**
     * 登录状态认证
     * @param request 请求响应
     * @param target 跳转连接
     * @return
     */
    Map<String, String> getAuthStatus(HttpServletRequest request, String target, HttpServletResponse response) throws TokenException;

    /**
     * 登出
     * @param request
     * @param response
     * @return
     */
    void logout(HttpServletRequest request, HttpServletResponse response);
}
