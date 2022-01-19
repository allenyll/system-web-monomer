package com.allenyll.sw.admin.controller.order;

import com.allenyll.sw.common.util.Result;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.common.entity.order.Order;
import com.allenyll.sw.common.entity.order.OrderDetail;
import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.common.util.StringUtil;
import com.allenyll.sw.system.service.order.impl.OrderDetailServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Api(tags = "订单详情接口")
@Controller
@RequestMapping("orderDetail")
public class OrderDetailController extends BaseController<OrderDetailServiceImpl, OrderDetail> {


    @Autowired
    OrderDetailServiceImpl orderDetailService;

    @ApiOperation("[小程序接口]根据订单ID获取订单详情")
    @ResponseBody
    @RequestMapping(value = "/getOrderDetail/{orderId}", method = RequestMethod.POST)
    public Result getOrderDetail(@PathVariable Long orderId){
        Result result = new Result();
        Map<String, Object> data = new HashMap<>();
        if(StringUtil.isEmpty(orderId)){
            result.fail("订单不存在!");
            return result;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);
        try {
            Order order = orderDetailService.getOrderDetail(params);
            data.put("order", order);
        } catch (Exception e) {
            log.error(e.getMessage());
            result.fail("系统异常，请联系管理员！");
            return result;
        }
        result.setData(data);
        return result;
    }
}
