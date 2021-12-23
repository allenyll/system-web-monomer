package com.allenyll.sw.sso.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:  Cookie工具类
 * @Author:       allenyll
 * @Date:         2020/8/25 10:53 上午
 * @Version:      1.0
 */
public class CookieUtil {


    /**
     * 设置Cookie
     * @param response 返回响应
     * @param domain 域名
     * @param path 路径
     * @param name cookie名称
     * @param value cookie值
     * @param maxAge cookie生命周期，以秒为单位
     * @param httpOnly 访问限制
     */
    public static void setCookie(HttpServletResponse response, String domain, String path, String name,
                                 String value, int maxAge, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        response.addCookie(cookie);
    }

    /**
     * 根据指定名称，从request中获取Cookie
     * @param request 请求属性
     * @param cookieNames cookie名称集合
     * @return 匹配的cookie集合
     */
    public static Map<String, String> getCookie(HttpServletRequest request, String ...cookieNames) {
        Map<String, String> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie:cookies) {
                String cookieName = cookie.getName();
                String cookieValue = cookie.getValue();
                for (String name : cookieNames) {
                    if (name.equals(cookieName)) {
                        cookieMap.put(cookieName, cookieValue);
                    }
                }
            }
        }
        return cookieMap;
    }
}
