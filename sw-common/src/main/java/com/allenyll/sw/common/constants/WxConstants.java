package com.allenyll.sw.common.constants;

/**
* @Title: WxConstants
* @Package com.allenyll.sw.common.constants
* @Description: 微信相关常量
* @author yu.leilei
* @date 2018/10/22 14:56
* @version V1.0
*/
public class WxConstants {

    public WxConstants(){}

    /**
     * 微信jwt验证缓存key
     */
    public static final String WX_JWT_MARK = "wx_jwt_key";

    /**
     * 微信jwt验证缓存值
     */
    public static final String WX_JWT = "wx_jwt";

    public static final String SUCCESS = "SUCCESS";

    public static final String FAIL = "FAIL";
    
    // 微信支付--支付中
    public static final String USERPAYING = "USERPAYING";

    public static final String REFUND_NOTIFY_URL = "https://www.allenyll.com/api-web/pay/payRefundCallback";
}
