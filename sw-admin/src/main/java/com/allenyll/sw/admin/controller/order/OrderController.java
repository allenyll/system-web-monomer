package com.allenyll.sw.admin.controller.order;

import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.common.enums.dict.OrderStatusDict;
import com.allenyll.sw.common.enums.dict.OrderTypeDict;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.system.service.order.IOrderService;
import com.allenyll.sw.system.service.order.impl.OrderServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.allenyll.sw.common.dto.OrderQueryDto;
import com.allenyll.sw.common.dto.OrderReturnDto;
import com.allenyll.sw.common.entity.order.Order;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags = "订单管理")
@RestController
@RequestMapping("order")
public class OrderController extends BaseController<OrderServiceImpl, Order> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    IOrderService orderService;

    @Override
    @ApiOperation("分页查询订单")
    @RequestMapping(value = "page", method = RequestMethod.GET)
    public DataResponse page(@RequestParam Map<String, Object> params){
        Map<String, Object> result = new HashMap<>();
        int total = orderService.selectCount(params);
        List<Map<String, Object>>  orderList = orderService.getOrderPage(params);
        result.put("total", total);
        result.put("list", orderList);
        return DataResponse.success(result);
    }

    @ApiOperation("删除订单")
    @RequestMapping(value = "deleteOrder", method = RequestMethod.POST)
    public DataResponse deleteOrder(@CurrentUser(isFull = true) User user, @RequestParam Map<String, Object> params){
        LOGGER.info("deleteOrder=>params："+params);
        params.put("userId", user.getId());
        DataResponse dataResponse = orderService.deleteOrder(params);
        return dataResponse;
    }

    @ApiOperation("订单发货")
    @RequestMapping(value = "deliveryOrder", method = RequestMethod.POST)
    public DataResponse deliveryOrder(@CurrentUser(isFull = true) User user, @RequestParam Map<String, Object> params){
        LOGGER.info("deliveryOrder=>params："+params);
        params.put("userId", user.getId());
        DataResponse dataResponse = orderService.deliveryOrder(params);
        return dataResponse;
    }

    @ApiOperation("关闭订单")
    @RequestMapping(value = "closeOrder", method = RequestMethod.POST)
    public DataResponse closeOrder(@CurrentUser(isFull = true) User user, @RequestParam Map<String, Object> params){
        LOGGER.info("closeOrder=>params："+params);
        params.put("userId", user.getId());
        DataResponse dataResponse = orderService.closeOrder(params);
        return dataResponse;
    }

    @ApiOperation("修改订单金额")
    @RequestMapping(value = "updateMoneyInfo", method = RequestMethod.POST)
    public DataResponse updateMoneyInfo(@CurrentUser(isFull = true) User user, @RequestParam Map<String, Object> params){
        LOGGER.info("updateMoneyInfo=>params："+params);
        params.put("userId", user.getId());
        DataResponse dataResponse = orderService.updateMoneyInfo(params);
        return dataResponse;
    }

    @ApiOperation("更新收货信息")
    @RequestMapping(value = "updateReceiverInfo", method = RequestMethod.POST)
    public DataResponse updateReceiverInfo(@CurrentUser(isFull = true) User user, @RequestParam Map<String, Object> params){
        LOGGER.info("updateReceiverInfo=>params："+params);
        params.put("userId", user.getId());
        DataResponse dataResponse = orderService.updateReceiverInfo(params);
        return dataResponse;
    }

    @ApiOperation("修改订单备注")
    @RequestMapping(value = "updateOrderNote", method = RequestMethod.POST)
    public DataResponse updateOrderNote(@CurrentUser(isFull = true) User user, @RequestParam Map<String, Object> params){
        LOGGER.info("updateOrderNote=>params："+params);
        params.put("userId", user.getId());
        DataResponse dataResponse = orderService.updateOrderNote(params);
        return dataResponse;
    }

    @ApiOperation("全量获取订单")
    @RequestMapping(value = "/getAllOrderList", method = RequestMethod.POST)
    public DataResponse getAllOrderList(@RequestBody Map<String, Object> params){
        Map<String, Object> result =  new HashMap<>();
        page = Integer.parseInt(params.get("page").toString());
        limit = Integer.parseInt(params.get("limit").toString());

        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("IS_DELETE", 0);
        wrapper.eq("ORDER_TYPE", OrderTypeDict.ONLINE.getCode());
        wrapper.eq("CUSTOMER_ID", MapUtil.getString(params, "customerId"));

        int total = service.count(wrapper);
        Page<Order> list = service.page(new Page<>(page, limit), wrapper);

        result.put("total", total);
        result.put("list", list.getRecords());

        return DataResponse.success(result);
    }

    @ApiOperation("取消订单")
    @RequestMapping(value = "cancelOrder", method = RequestMethod.POST)
    public DataResponse cancelOrder(@CurrentUser(isFull = true) User user, @RequestParam Map<String, Object> params){
        LOGGER.info("cancelOrder=>params："+params);
        params.put("userId", user.getId());
        DataResponse dataResponse = orderService.cancelOrder(params);
        return dataResponse;
    }

    @ApiOperation("根据订单ID获取订单")
    @RequestMapping(value = "/selectById", method = RequestMethod.POST)
    public Order selectById(@RequestParam String orderId) {
        return service.getById(orderId);
    }

    @ApiOperation("更新订单支付状态")
    @RequestMapping(value = "/updateById", method = RequestMethod.POST)
    public void updateById(@RequestBody Order order) {
        service.updateById(order);
    }

    @ApiOperation("[小程序接口]未支付订单放到消息队列")
    @ResponseBody
    @RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
    public Result sendMessage(@CurrentUser(isFull = true) User user, @RequestBody Map<String, Object> params) {
        params.put("userId", user.getId());
        service.sendMessage(params);
        return new Result();
    }

    @ApiOperation("[小程序接口]创建订单")
    @RequestMapping(value = "/createOrder", method = RequestMethod.POST)
    public Result createOrder(@CurrentUser(isFull = true) User user, @RequestBody Map<String, Object> param){
        Result result = new Result();
        Map<String, Object> data =  new HashMap<>();
        param.put("userId", user.getId());
        log.info("缓存订单参数: {}"+param);
        Order order = new Order();
        try {
            orderService.createOrder(order, param);
        } catch (Exception e) {
            log.error("订单创建失败：" + e.getMessage());
            result.fail("系统异常，订单创建失败，请联系管理员");
            return result;
        }

        data.put("order", order);
        result.setData(data);
        return result;
    }

    @ApiOperation("[小程序接口]获取订单信息")
    @ResponseBody
    @RequestMapping(value = "/getOrderInfo", method = RequestMethod.POST)
    public Result<OrderReturnDto> getOrderInfo(@CurrentUser(isFull = true) User user, @RequestBody OrderQueryDto queryDto) {
        return service.getOrderInfo(user, queryDto);
    }

    @ApiOperation("[小程序接口]获取各类型订单数量")
    @RequestMapping(value = "/getOrderNum", method = RequestMethod.POST)
    public Result getOrderNum(@RequestBody Map<String, Object> params){
        Result result = new Result();
        Map<String, Object> data =  new HashMap<>();

        int unPayNum = orderService.getUnPayNum(params);
        int unReceiveNum = orderService.getUnReceiveNum(params);
        int deliveryNum = orderService.getDeliveryNum(params);
        int receiveNum = orderService.getReceiveNum(params);
        int appraisesNum = orderService.getAppraisesNum(params);
        int finishNum = orderService.getFinishNum(params);
        data.put("unPayNum", unPayNum);
        data.put("unReceiveNum", unReceiveNum);
        data.put("deliveryNum", deliveryNum);
        data.put("receiveNum", receiveNum);
        data.put("appraisesNum", appraisesNum);
        data.put("finishNum", finishNum);
        result.setData(data);
        return result;
    }

    @ApiOperation("[小程序接口]获取订单列表")
    @RequestMapping(value = "/getOrderList", method = RequestMethod.POST)
    public Result getOrderList(@RequestBody OrderQueryDto queryDto){
        Result result = new Result();
        Map<String, Object> data =  new HashMap<>();
        List<Order> list = orderService.getOrderList(queryDto);
        data.put("list", list);
        result.setData(data);
        return result;
    }

    @ApiOperation("[小程序接口]取消订单")
    @RequestMapping(value = "cancelMiniOrder", method = RequestMethod.POST)
    public Result cancelMiniOrder(@CurrentUser(isFull = true) User user, @RequestBody Map<String, Object> params){
        LOGGER.info("cancelOrder=>params："+params);
        Result result = new Result();
        params.put("userId", user.getId());
        DataResponse dataResponse = orderService.cancelOrder(params);
        if (dataResponse.isSuccess()) {
            return result;
        } else {
            result.fail("系统异常，请联系管理员!");
            return result;
        }
    }

    @ApiOperation("[小程序接口]删除订单")
    @RequestMapping(value = "/deleteOrder/{orderId}", method = RequestMethod.POST)
    public Result deleteOrder(@PathVariable String orderId){
        Result result = new Result();
        if(StringUtil.isEmpty(orderId)){
           log.error("缺少必要参数orderId！");
           result.fail("取消订单失败！");
           return result;
        }
        try {
            Order order = orderService.getById(orderId);
            order.setIsDelete(1);
            order.setUpdateTime(DateUtil.getCurrentDateTime());
            orderService.updateById(order);
        } catch (Exception e) {
            log.error(e.getMessage());
            result.fail("系统异常，订单删除失败!");
            return result;
        }
        return result;
    }

    @ApiOperation("[小程序接口]订单收货")
    @RequestMapping(value = "/receiveOrder/{orderId}", method = RequestMethod.POST)
    public Result receiveOrder(@PathVariable String orderId){
        Result result = new Result();
        if(StringUtil.isEmpty(orderId)){
            log.error("缺少必要参数orderId！");
            result.fail("取消订单失败！");
            return result;
        }
        String time = DateUtil.getCurrentDateTime();
        try {
            Order order = orderService.getById(orderId);
            order.setOrderStatus(OrderStatusDict.RECEIVE.getCode());
            order.setReceiveTime(time);
            order.setUpdateTime(time);
            orderService.updateById(order);
        } catch (Exception e) {
            log.error(e.getMessage());
            result.fail("系统异常，订单收货失败，请联系管理员!");
            return result;
        }
        return result;
    }

}
