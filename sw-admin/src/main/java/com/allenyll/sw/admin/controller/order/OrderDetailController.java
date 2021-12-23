package com.allenyll.sw.admin.controller.order;

import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.common.entity.order.Order;
import com.allenyll.sw.common.entity.order.OrderDetail;
import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.common.util.StringUtil;
import com.allenyll.sw.system.service.order.impl.OrderDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("orderDetail")
public class OrderDetailController extends BaseController<OrderDetailServiceImpl, OrderDetail> {


    @Autowired
    OrderDetailServiceImpl orderDetailService;

    @ResponseBody
    @RequestMapping(value = "/getOrderDetail/{orderId}", method = RequestMethod.POST)
    public DataResponse getOrderDetail(@PathVariable Long orderId){
        Map<String, Object> result = new HashMap<>();
        if(StringUtil.isEmpty(orderId)){
            return DataResponse.fail("订单不存在!");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        try {
            Order order = orderDetailService.getOrderDetail(map);
            result.put("order", order);
        } catch (Exception e) {
            e.printStackTrace();
            return DataResponse.fail("获取订单失败");
        }

        return DataResponse.success(result);
    }
}
