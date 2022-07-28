package com.allenyll.sw.sso.controller;

import com.allenyll.sw.common.annotation.Log;
import com.allenyll.sw.common.util.Result;
import com.allenyll.sw.common.util.StringUtil;
import com.allenyll.sw.common.entity.auth.AuthToken;
import com.allenyll.sw.sso.exception.TokenException;
import com.allenyll.sw.sso.service.IAuthService;
import com.allenyll.sw.sso.util.CookieUtil;
import com.allenyll.sw.sso.util.EncryptUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * @Description:
 * @Author:       allenyll
 * @Date:         2020/8/20 2:22 下午
 * @Version:      1.0
 */
@Api(tags = "登录认证管理")
@Controller
@RequestMapping("/auth")
public class AuthController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    IAuthService authService;

    @Value("${auth.common.clientId}")
    private String clientId;

    @Value("${auth.common.clientSecret}")
    private String clientSecret;

    @Value("${auth.cookieDomain}")
    private String cookieDomain;


    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;

    private static final String MEDIA_TYPE = "application/javascript;charset=UTF-8";

    /**
     * 后端登录
     * @param username 用户名
     * @param password 密码
     * @param response 响应
     * @return token
     */
    @ApiOperation("登录")
    @Log(value = "登录", type = "SW0301")
    @ResponseBody
    @PostMapping("login")
    public Result<AuthToken> login(HttpServletRequest request, String username, String password, HttpServletResponse response) {
        Result<AuthToken> result = new Result<>();
        //校验参数
        if (StringUtil.isEmpty(username)){
            throw new RuntimeException("请输入用户名");
        }
        if (StringUtil.isEmpty(password)){
            throw new RuntimeException("请输入密码");
        }
        AuthToken authToken = new AuthToken();
        try {
            password = EncryptUtil.aesDecrypt(password, "o7H8uIM2O5qv65l2");
        } catch (Exception e) {
            LOGGER.error("解密失败：{}", e.getMessage());
        }
        try {
            authService.login(request, authToken, username, password, clientId, clientSecret);
        } catch (Exception e) {
            LOGGER.error("登录接口出现异常：{}", e);
            result.fail("登录失败");
            return result;
        }

        CookieUtil.setCookie(response, cookieDomain, "/", "uid", authToken.getJti(), cookieMaxAge, false);

        result.setData(authToken);
        return result;
    }

    @ApiOperation("登录页")
    @GetMapping("/loginPage")
    public String loginPage(@RequestParam(value = "from",required = false,defaultValue = "") String from, Model model) {
        model.addAttribute("from",from);
        return "login";
    }

    /**
     * 认证token是否有效，跳转到指定连接
     * @param target 跳转链接
     * @param request 请求头
     * @param response 响应
     */
    @ApiOperation("token认证")
    @ResponseBody
    @GetMapping("getAuthStatus")
    public void getAuthStatus(@RequestParam(value = "target",required = false,defaultValue = "") String target,
                              HttpServletRequest request, HttpServletResponse response) throws TokenException {
        Map<String, String> result = null;
        try {
            result = authService.getAuthStatus(request, target, response);
            if (result != null) {
                CookieUtil.setCookie(response, cookieDomain, "/", "authToken", result.get("authToken"), cookieMaxAge, false);
                // 跳转到指定地址
                try {
                    LOGGER.info("跳转到指定地址：{}", target);
                    response.sendRedirect(target);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (TokenException e) {
            LOGGER.error("token认证失败：{}", e.getMessage());
        }
    }

    /**
     * 判断登陆是否有效
     * @param request 请求头
     * @param response 响应
     */
    @ApiOperation("判断登录是否有效")
    @RequestMapping(value = "authStatus", method = RequestMethod.GET, produces= MEDIA_TYPE)
    public void authStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "No-cache");
        response.setDateHeader("Expires", 0L);
        response.setContentType("application/javascript;charset=UTF-8");
        String callback = request.getParameter("callback");
        if (StringUtil.isEmpty(callback)) {
            callback = "callback";
        }
        Map<String, String> result = null;
        try {
            result = authService.getAuthStatus(request, "", response);
            if (result == null) {
                response.getWriter().write(callback + "({" + "\"isLogin\":" + true + "})" + ";");
                response.getWriter().flush();
            } else {
                response.getWriter().write(callback + "({" + "\"isLogin\":" + true + "})" + ";");
                response.getWriter().flush();
            }
        } catch (TokenException e) {
            LOGGER.error("判断登陆是否有效异常：{}", e.getMessage());
            response.getWriter().write(callback + "({" + "\"isLogin\":" + false + "})" + ";");
            response.getWriter().flush();
        }
    }

    @ApiOperation("退出登录")
    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public void logout(HttpSession session, @RequestParam String service, HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        session.invalidate();
        try {
            response.sendRedirect(service);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
