package com.allenyll.sw.admin.controller.pay;

import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.system.service.order.impl.OrderServiceImpl;
import com.allenyll.sw.system.service.pay.impl.WxPaymentServiceImpl;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.*;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @Description:  微信支付接口
 * @Author:       allenyll
 * @Date:         2019/4/4 3:40 PM
 * @Version:      1.0
 */
@Controller
@Api(tags = "交易管理")
@RequestMapping("/pay")
public class WxPayController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WxPayController.class);

    @Autowired
    private WxPaymentServiceImpl wxPaymentService;

    @Autowired
    private OrderServiceImpl orderService;

    /**
     * 调用统一下单接口，并组装生成支付所需参数对象.
     *
     * @param request 统一下单请求参数
     * @return 返回 {@link com.github.binarywang.wxpay.bean.order}包下的类对象
     */
    @ApiOperation(value = "统一下单，并组装所需支付参数")
    @ResponseBody
    @PostMapping("/createOrder")
    public Result createOrder(@CurrentUser(isFull = true) User user, 
                              HttpServletRequest request, HttpServletResponse response) {
        return wxPaymentService.createOrder(user, request, response);
    }   

    @ResponseBody
    @ApiOperation("支付回调")
    @PostMapping("/payNotify")
    public String payNotify(HttpServletRequest request, HttpServletResponse response) {
        try {
           return wxPaymentService.payNotify(request, response);
        } catch (Exception e) {
            LOGGER.error("微信回调结果异常,异常原因{}", e.getMessage());
            return WxPayNotifyResponse.fail(e.getMessage());
        }
    }

    @Transactional
    @ResponseBody
    @ApiOperation("发起支付")
    @RequestMapping(value = "createUnifiedOrder", method = RequestMethod.POST)
    public Result<Map<String, Object>> createUnifiedOrder(@CurrentUser(isFull = true) User user, HttpServletRequest request, HttpServletResponse response) {
        return wxPaymentService.createUnifiedOrder(user, request, response);
    }

    /**
     * 微信查询订单状态
     */
    @ApiOperation(value = "查询订单状态")
    @PostMapping("query")
    @ResponseBody
    public Result orderQuery(@CurrentUser(isFull = true) CurrentUser currentUser, Long orderId, Long transactionId) {
       return wxPaymentService.queryOrder(currentUser, orderId, transactionId);
    }

    @ResponseBody
    @ApiOperation("支付签名")
    @RequestMapping(value = "sign", method = RequestMethod.POST)
    public DataResponse sign(HttpServletRequest request, HttpServletResponse response) {
        return wxPaymentService.paySign(request, response);
    }

    @ResponseBody
    @ApiOperation("更细订单状态")
    @RequestMapping(value = "/updateStatus",method = RequestMethod.POST)
    public DataResponse updateObj(@RequestBody Map<String, Object> params){
        return orderService.updateOrderStatus(params);
    }
}
