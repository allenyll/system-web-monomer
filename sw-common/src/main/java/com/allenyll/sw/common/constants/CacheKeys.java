package com.allenyll.sw.common.constants;

/**
 * @Description:  缓存键
 * @Author:       allenyll
 * @Date:         2020/6/30 10:45 上午
 * @Version:      1.0
 */
public class CacheKeys {

    public CacheKeys(){}

    /**
     * 商城缓存
     */
    public static final String WX_CACHE_ZONE = "SW_WX";

    /**
     * 系统缓存
     */
    public static final String SYS_CACHE_ZONE = "SYSTEM_WEB";

    /**
     * 订单支付结果查询
     */
    public static final String ORDER_PAY = "ORDER_PAY";

    /**
     * 微信jwt验证缓存key
     */
    public static final String WX_JWT_MARK = "wx_jwt_key";

    /**
     * 微信jwt验证缓存值
     */
    public static final String WX_JWT = "wx_jwt";

    /**
     * 当前登录微信用户openid
     */
    public static final String WX_CURRENT_OPENID = "wx_current_openid";

    /**
     * 当前登录用户id
     */
    public static final String CURRENT_USER_ID = "current_user_id_";


}
