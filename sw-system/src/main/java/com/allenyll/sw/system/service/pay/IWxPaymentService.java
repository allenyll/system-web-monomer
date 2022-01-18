package com.allenyll.sw.system.service.pay;

import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.common.dto.TransactionRefundDto;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.common.util.Result;
import com.github.binarywang.wxpay.exception.WxPayException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @Description:  小程序支付接口
 * @Author:       allenyll
 * @Date:         2020/11/23 上午11:54
 * @Version:      1.0
 */
public interface IWxPaymentService {

    /**
     * 微信支付统一下单
     * @param user
     * @param request
     * @param response
     * @return
     */
    Result createOrder(User user, HttpServletRequest request, HttpServletResponse response);

    /**
     * 微信支付回调
     * @param request
     * @param response
     * @return
     */
    String payNotify(HttpServletRequest request, HttpServletResponse response) throws WxPayException, Exception;

    /**
     * 统一下单--自定义
     * @param user
     * @param request
     * @param response
     * @return
     */
    Result<Map<String, Object>> createUnifiedOrder(User user, HttpServletRequest request, HttpServletResponse response);

    /**
     * 查询订单--自定义
     * @param currentUser
     * @param orderId
     * @param transactionId
     * @return
     */
    Result queryOrder(CurrentUser currentUser, Long orderId, Long transactionId);

    /**
     * 签名--自定义
     * @param request
     * @param response
     * @return
     */
    DataResponse paySign(HttpServletRequest request, HttpServletResponse response);
}
