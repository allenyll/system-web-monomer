package com.allenyll.sw.common.entity.auth;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @Author:       allenyll
 * @Date:         2020/8/21 5:25 下午
 * @Version:      1.0
 */
@Data
public class AuthToken implements Serializable {

    /**
     * 令牌信息 jwt
     */
    private String accessToken;
    /**
     * 刷新token(refresh_token)
     */
    private String refreshToken;
    /**
     * jwt短令牌
     */
    private String jti;

    /**
     * 微信openid
     */
    private String openid;
}
