package com.allenyll.sw.sso.util;

import java.util.Base64;

/**
 * @Description:
 * @Author:       allenyll
 * @Date:         2020/8/24 11:52 上午
 * @Version:      1.0
 */
public class AuthUtil {

    /**
     *  将clientId和clientSecret组合转换成base64
     * @param clientId 客户端id
     * @param clientSecret 客户端密码
     * @return 加密信息
     */
    public static String getHttpBasic(String clientId, String clientSecret) {
        String value = clientId + ":" + clientSecret;
        return "Basic " + Base64.getEncoder().encodeToString(value.getBytes());
    }

}
