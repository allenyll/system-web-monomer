package com.allenyll.sw.sso.exception;

/**
 * @Description:  登录认证异常处理
 * @Author:       allen yll
 * @Date:         2021/7/13 下午1:59
 * @Version:      1.0
 */
public class TokenException extends Exception{

    private String message;
    
    public TokenException(String message) {
        super(message);
    }
}
