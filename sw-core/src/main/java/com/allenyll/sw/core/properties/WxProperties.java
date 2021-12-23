package com.allenyll.sw.core.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
* @Title: wxProperties
* @Package com.allenyll.sw.wx.properties
* @Description: TODO
* @author yu.leilei
* @date 2018/10/19 11:44
* @version V1.0
*/
@Data
@Configuration
public class WxProperties {

    @Value("${auth.wx.sessionHost}")
    private String sessionHost;

    @Value("${auth.wx.appId}")
    private String appId;

    @Value("${auth.wx.appSecret}")
    private String appSecret;

    @Value("${auth.wx.grantType}")
    private String grantType;

    @Value("${auth.wx.systemWebUrl}")
    private String systemWebUrl;

    @Value("${auth.wx.username}")
    private String username;

    @Value("${auth.wx.password}")
    private String password;

    @Value("${auth.wx.mchId}")
    private String mchId;

    @Value("${auth.wx.key}")
    private String key;

    @Value("${auth.wx.orderUrl}")
    private String orderUrl;

    @Value("${auth.wx.orderQuery}")
    private String orderQuery;

    @Value("${auth.wx.refundUrl}")
    private String refundUrl;

    @Value("${auth.wx.signType}")
    private String signType;

    @Value("${auth.wx.tradeType}")
    private String tradeType;

}
