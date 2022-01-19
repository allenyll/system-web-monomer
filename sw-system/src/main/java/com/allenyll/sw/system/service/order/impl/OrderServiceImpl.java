package com.allenyll.sw.system.service.order.impl;

import com.allenyll.sw.system.base.IUserService;
import com.allenyll.sw.system.base.impl.DictServiceImpl;
import com.allenyll.sw.system.mapper.order.OrderMapper;
import com.allenyll.sw.system.producer.OrderProducer;
import com.allenyll.sw.system.service.order.IOrderAftersaleService;
import com.allenyll.sw.system.service.order.IOrderService;
import com.allenyll.sw.system.service.pay.impl.TransactionServiceImpl;
import com.allenyll.sw.system.service.product.IGoodsService;
import com.allenyll.sw.system.service.product.ISkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.common.enums.dict.*;
import com.allenyll.sw.common.dto.OrderQueryDto;
import com.allenyll.sw.common.dto.OrderReturnDto;
import com.allenyll.sw.common.entity.order.Order;
import com.allenyll.sw.common.entity.order.OrderAftersale;
import com.allenyll.sw.common.entity.order.OrderDetail;
import com.allenyll.sw.common.entity.order.OrderOperateLog;
import com.allenyll.sw.common.entity.pay.Transaction;
import com.allenyll.sw.common.entity.product.Goods;
import com.allenyll.sw.common.entity.product.Sku;
import com.allenyll.sw.common.entity.system.Dict;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单基础信息表
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-06-26 21:11:07
 */
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private static final long DELAY_TIMES = 10 * 1000;

    @Resource
    OrderMapper orderMapper;

    @Autowired
    OrderDetailServiceImpl orderDetailService;

    @Autowired
    IUserService userService;

    @Autowired
    DictServiceImpl dictService;
    
    @Autowired
    OrderOperateLogServiceImpl orderOperateLogService;

    @Autowired
    OrderProducer orderProducer;

    @Autowired
    private IOrderAftersaleService orderAftersaleService;

    @Autowired
    private ISkuService skuService;

    @Autowired
    private IGoodsService goodsService;
    
    @Autowired
    private TransactionServiceImpl transactionService;

    @Override
    public void createOrder(Order order, Map<String, Object> param) {
        String time = DateUtil.getCurrentDateTime();
        Long orderId = SnowflakeIdWorker.generateId();
        order.setId(orderId);
        order.setOrderNo(StringUtil.getOrderNo());
        order.setAfterStatus(OrderSaleDict.START.getCode());
        order.setOrderType(OrderTypeDict.ONLINE.getCode());
        order.setAutoConfirmDay(7);
        order.setConfirmStatus(OrderConfirmDict.UN_CONFIRM.getCode());
        order.setOrderStatus(OrderStatusDict.START.getCode());
        order.setSettlementStatus(OrderSettleDict.UN_SETTLE.getCode());
        order.setOrderTime(time);
        order.setAddTime(time);
        order.setUpdateTime(time);
        order.setIsDelete(0);

        order.setIntegration(MapUtil.getInteger(param, "goodsIntegral"));
        order.setGrowth(MapUtil.getInteger(param, "giftGrowth"));
        order.setGoodsCount(MapUtil.getInteger(param, "goodsCount"));

        order.setCouponAmount(new BigDecimal(MapUtil.getString(param, "couponAmount")));
        order.setDiscountAmount(new BigDecimal(MapUtil.getString(param, "discountAmount")));
        order.setPromotionAmount(new BigDecimal(MapUtil.getString(param, "promotionAmount")));
        order.setIntegrationAmount(new BigDecimal(MapUtil.getString(param, "integrationAmount")));
        order.setLogisticsFee(new BigDecimal(MapUtil.getString(param, "logisticsFee")));
        order.setTotalAmount(new BigDecimal(MapUtil.getString(param, "totalAmount")));
        order.setOrderAmount(new BigDecimal(MapUtil.getString(param, "allGoodsPrice")));
        order.setPayAmount(new BigDecimal(MapUtil.getString(param, "totalAmount")));

        order.setAddressId(MapUtil.getLong(param, "addressId"));
        order.setCouponId(MapUtil.getLong(param, "couponId"));
        order.setCustomerId(MapUtil.getLong(param, "customerId"));

        order.setOrderRemark(MapUtil.getString(param, "remark"));

        order.setReceiverName(MapUtil.getString(param, "name"));
        order.setReceiverPhone(MapUtil.getString(param, "phone"));
        order.setReceiverPostCode(MapUtil.getString(param, "postCode"));
        order.setReceiverProvince(MapUtil.getString(param, "province"));
        order.setReceiverCity(MapUtil.getString(param, "city"));
        order.setReceiverRegion(MapUtil.getString(param, "region"));
        order.setReceiverDetailAddress(MapUtil.getString(param, "detailAddress"));

        try {
            orderMapper.insert(order);
            dealGoodsList(order, param);
        } catch (Exception e) {
            LOGGER.info("订单创建异常");
            e.printStackTrace();
            return;
        }
        LOGGER.info("cacheOrder: ", order);
    }


    public void dealGoodsList(Order order, Map<String, Object> param) {
        String goodsJsonStr = MapUtil.getString(param, "goodsList");
        JSONArray jsonArray = JSONArray.fromObject(goodsJsonStr);
        if (jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                addOrderDetail(order, json);
            }
        }

    }

    private void addOrderDetail(Order order, JSONObject json) {

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setId(SnowflakeIdWorker.generateId());
        orderDetail.setCategoryId(Long.parseLong(json.getString("categoryId")));
        orderDetail.setGoodsId(Long.parseLong(json.getString("goodsId")));
        orderDetail.setOrderId(order.getId());
        orderDetail.setGoodsName(json.getString("name"));
        orderDetail.setGoodsPrice(new BigDecimal(json.getString("price")));
        orderDetail.setOrderNo(order.getOrderNo());
        orderDetail.setPic(json.getString("pic"));
        orderDetail.setSkuId(Long.parseLong(json.getString("skuId")));
        orderDetail.setQuantity(json.getInt("number"));
        orderDetail.setSkuCode(json.getString("skuCode"));
        orderDetail.setDeductStockType("");
        orderDetail.setStatus(OrderSaleDict.START.getCode());
        orderDetail.setBrandName(json.getString("brandName"));
        orderDetail.setContent("");
        orderDetail.setTotalAmount(new BigDecimal("0"));
        orderDetail.setCouponAmount(new BigDecimal("0"));
        orderDetail.setIntegrationAmount(new BigDecimal("0"));
        orderDetail.setPromotionAmount(new BigDecimal("0"));
        orderDetail.setPromotionName("");
        orderDetail.setRealAmount(new BigDecimal("0"));
        orderDetail.setSpecValue(json.getString("specValue"));
        orderDetail.setAttributes(json.getString("selectSpecOption").replace("\"", "").replace("[", "").replace("]", ""));
        orderDetail.setRemark("");

        orderDetail.setIsDelete(0);
        orderDetail.setAddTime(order.getOrderTime());
        orderDetail.setUpdateTime(order.getOrderTime());


        try {
            orderDetailService.save(orderDetail);
        } catch (Exception e) {
            LOGGER.info("订单商品明细新增异常");
            e.printStackTrace();
            return;
        }

    }

    @Override
    public int getUnPayNum(Map<String, Object> params) {
        Integer num = orderMapper.getUnPayNum(params);
        return num == null ? 0 : num.intValue();
    }

    @Override
    public int getUnReceiveNum(Map<String, Object> params) {
        Integer num = orderMapper.getUnReceiveNum(params);
        return num == null ? 0 : num.intValue();
    }

    @Override
    public int getReceiveNum(Map<String, Object> params) {
        Integer num = orderMapper.getReceiveNum(params);
        return num == null ? 0 : num.intValue();
    }

    @Override
    public int getAppraisesNum(Map<String, Object> params) {
        Integer num = orderMapper.getAppraisesNum(params);
        return num == null ? 0 : num.intValue();
    }

    @Override
    public int getFinishNum(Map<String, Object> params) {
        Integer num = orderMapper.getFinishNum(params);
        return num == null ? 0 : num.intValue();
    }

    @Override
    public int getDeliveryNum(Map<String, Object> params) {
        Integer num = orderMapper.getDeliveryNum(params);
        return num == null ? 0 : num.intValue();
    }

    @Override
    public List<Order> getOrderList(OrderQueryDto queryDto) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("IS_DELETE", 0);
        wrapper.eq("ORDER_TYPE", "SW0601");
        wrapper.eq("CUSTOMER_ID", queryDto.getCustomerId());
        wrapper.orderBy(true, false, "ORDER_TIME");

        String  type = queryDto.getType();
        if ("AFTERSALE".equals(type)) {
            wrapper.in("ORDER_STATUS", Arrays.asList("SW0701", "SW0702", "SW0703", "SW0704", "SW0705", "SW0706"));
        }

        List<Order> list = orderMapper.selectList(wrapper);

        dealList(list);

        return list;
    }

    @Override
    public DataResponse cancelOrder(Map<String, Object> map) {
        Long userId = MapUtil.getLong(map, "userId");
        String optName = MapUtil.getString(map, "opt_name");
        Long orderId = MapUtil.getLong(map, "orderId");
        String note = MapUtil.getString(map, "note");
        String time = DateUtil.getCurrentDateTime();
        try {
            Order order = orderMapper.selectById(orderId);
            order.setNote(note);
            order.setUpdateUser(userId);
            order.setUpdateTime(DateUtil.getCurrentDateTime());
            // 未支付的订单才执行
            if (OrderStatusDict.START.getCode().equals(order.getOrderStatus())) {
                order.setOrderStatus(OrderStatusDict.CANCEL.getCode());
                orderMapper.updateById(order);
                // 记录日志
                dealOperateLog(userId, time, order, note, "cancel", optName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DataResponse.fail("订单取消失败");
        }
        return DataResponse.success();
    }

    @Override
    public void sendMessage(Map<String, Object> params) {
        //放入消息队列
        try {
            orderProducer.sendMessage(params, DELAY_TIMES);
        } catch (Exception e) {
            log.error("订单投放到消息队列失败！");
        }
    }

    @Override
    public Result<OrderReturnDto> getOrderInfo(User user, OrderQueryDto queryDto) {
        Result<OrderReturnDto> result = new Result<>();
        Order order = orderMapper.selectById(queryDto.getOrderId());
        OrderDetail orderDetail = orderDetailService.getById(queryDto.getOrderDetailId());
        OrderReturnDto orderReturnDto = new OrderReturnDto();
        orderReturnDto.setOrder(order);
        orderReturnDto.setOrderDetail(orderDetail);
        result.setData(orderReturnDto);
        return result;
    }

    private void dealList(List<Order> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        for (Order order : list) {
            Map<String, Object> _map = new HashMap<>();
            _map.put("orderId", order.getId());
            Dict statusDict = dictService.getDictByCode(order.getOrderStatus());
            if (statusDict != null) {
                order.setStatusStr(statusDict.getName());
            }
            // 获取订单明细
            List<OrderDetail> orderDetails = orderDetailService.getOrderDetailList(_map);
            if (CollectionUtil.isEmpty(orderDetails)) {
                continue;
            }
            for (OrderDetail orderDetail:orderDetails) {
                if (!OrderSaleDict.START.getCode().equals(orderDetail.getStatus()) && !OrderSaleDict.CANCEL.getCode().equals(orderDetail.getStatus())) {
                    QueryWrapper<OrderAftersale> wrapper = new QueryWrapper<>();
                    wrapper.eq("ORDER_DETAIL_ID", orderDetail.getId());
                    wrapper.ne("AFTERSALE_STATUS", OrderSaleDict.START.getCode());
                    wrapper.ne("AFTERSALE_STATUS", OrderSaleDict.CANCEL.getCode());
                    OrderAftersale aftersale = orderAftersaleService.getOne(wrapper);
                    if (aftersale != null) {
                        orderDetail.setOrderAftersaleId(aftersale.getId());
                    }
                }
            }
            order.setOrderDetails(orderDetails);
        }
        
    }

    @Override
    public int selectCount(Map<String, Object> params) {
        Integer num = orderMapper.selectCount(params);
        return num == null ? 0 : num.intValue();
    }

    @Override
    public List<Map<String, Object>> getOrderPage(Map<String, Object> params) {
        int page = Integer.parseInt(params.get("page").toString());
        int limit = Integer.parseInt(params.get("limit").toString());
        int start = (page - 1) * limit;
        params.put("start", start);
        params.put("limit", limit);
        params.put("year", DateUtil.getCurrentDate().substring(4));
        List<Map<String, Object>> orderList = orderMapper.getOrderPage(params);
        return orderList;
    }

    @Override
    public DataResponse deleteOrder(Map<String, Object> params) {
        Long userId = MapUtil.getLong(params, "userId");
        String ids = MapUtil.getString(params, "ids");
        String[] idArr = ids.split(",");
        String time = DateUtil.getCurrentDateTime();
        if (idArr.length > 0) {
            for (int i = 0; i < idArr.length; i++) {
                try {
                    Order order = orderMapper.selectById(idArr[i]);
                    order.setIsDelete(1);
                    order.setUpdateUser(userId);
                    order.setUpdateTime(DateUtil.getCurrentDateTime());
                    orderMapper.updateById(order);
                    // 记录日志
                    dealOperateLog(userId, time, order, "", "delete", "");
                } catch (Exception e) {
                    e.printStackTrace();
                    return DataResponse.fail("订单删除失败");
                }
            }
        }
        return DataResponse.success();
    }

    @Override
    public DataResponse closeOrder(Map<String, Object> params) {
        Long userId = MapUtil.getLong(params, "userId");
        String ids = MapUtil.getString(params, "ids");
        String note = MapUtil.getString(params, "note");
        String[] idArr = ids.split(",");
        String time = DateUtil.getCurrentDateTime();
        if (idArr.length > 0) {
            for (int i = 0; i < idArr.length; i++) {
                try {
                    Order order = orderMapper.selectById(idArr[i]);
                    order.setOrderStatus(OrderStatusDict.CLOSE.getCode());
                    order.setNote(note);
                    order.setUpdateUser(userId);
                    order.setUpdateTime(time);
                    orderMapper.updateById(order);
                    // 记录日志
                    dealOperateLog(userId, time, order, note, "close", "");
                } catch (Exception e) {
                    e.printStackTrace();
                    return DataResponse.fail("订单关闭失败");
                }
            }
        }
        return DataResponse.success();
    }

    @Override
    public DataResponse deliveryOrder(Map<String, Object> params) {
        Long userId = MapUtil.getLong(params, "userId");
        String time = DateUtil.getCurrentDateTime();
        String orderId = MapUtil.getString(params, "orderId");
        if (StringUtil.isEmpty(orderId)) {
            return DataResponse.fail("订单不存在");
        }
        try {
            Order order = orderMapper.selectById(orderId);
            order.setDeliveryCompany(MapUtil.getString(params, "deliveryCompany"));
            order.setDeliveryNo(MapUtil.getString(params, "deliveryNo"));
            order.setDeliveryTime(time);
            order.setOrderStatus(OrderStatusDict.DELIVERY.getCode());
            order.setUpdateUser(userId);
            order.setUpdateTime(time);
            orderMapper.updateById(order);
            // 记录日志
            dealOperateLog(userId, time, order, "", "delivery", "");
        } catch (Exception e) {
            e.printStackTrace();
            return DataResponse.fail("订单发货失败");
        }
        return DataResponse.success();
    }

    @Override
    public DataResponse updateMoneyInfo(Map<String, Object> params) {
        Long userId = MapUtil.getLong(params, "userId");
        String time = DateUtil.getCurrentDateTime();
        String orderId = MapUtil.getString(params, "orderId");
        if (StringUtil.isEmpty(orderId)) {
            return DataResponse.fail("订单不存在");
        }
        try {
            Order order = orderMapper.selectById(orderId);
            BigDecimal logisticsFee = new BigDecimal(MapUtil.getString(params, "logisticsFee"));
            BigDecimal discountAmount = new BigDecimal(MapUtil.getString(params, "discountAmount"));
            order.setLogisticsFee(logisticsFee);
            order.setDiscountAmount(discountAmount);
            order.setTotalAmount(order.getTotalAmount().add(logisticsFee).subtract(discountAmount));
            order.setPayAmount(order.getPayAmount().add(logisticsFee).subtract(discountAmount));
            order.setUpdateUser(userId);
            order.setUpdateTime(time);
            orderMapper.updateById(order);
            // 记录日志
            dealOperateLog(userId, time, order, "", "money", "");
        } catch (Exception e) {
            e.printStackTrace();
            return DataResponse.fail("订单修改金额失败");
        }
        return DataResponse.success();
    }

    @Override
    public DataResponse updateReceiverInfo(Map<String, Object> params) {
        Long userId = MapUtil.getLong(params, "userId");
        String time = DateUtil.getCurrentDateTime();
        String orderId = MapUtil.getString(params, "orderId");
        if (StringUtil.isEmpty(orderId)) {
            return DataResponse.fail("订单不存在");
        }
        try {
            Order order = orderMapper.selectById(orderId);
            order.setReceiverName(MapUtil.getString(params, "receiverName"));
            order.setReceiverPhone(MapUtil.getString(params, "receiverPhone"));
            order.setReceiverPostCode(MapUtil.getString(params, "receiverPostCode"));
            order.setReceiverProvince(MapUtil.getString(params, "receiverProvince"));
            order.setReceiverCity(MapUtil.getString(params, "receiverCity"));
            order.setReceiverRegion(MapUtil.getString(params, "receiverRegion"));
            order.setReceiverDetailAddress(MapUtil.getString(params, "receiverDetailAddress"));
            order.setUpdateUser(userId);
            order.setUpdateTime(time);
            orderMapper.updateById(order);
            // 记录日志
            dealOperateLog(userId, time, order, "", "receiver", "");
        } catch (Exception e) {
            e.printStackTrace();
            return DataResponse.fail("订单修改收货地址失败");
        }
        return DataResponse.success();
    }

    @Override
    public DataResponse updateOrderNote(Map<String, Object> params) {
        Long userId = MapUtil.getLong(params, "userId");
        String time = DateUtil.getCurrentDateTime();
        String orderId = MapUtil.getString(params, "orderId");
        if (StringUtil.isEmpty(orderId)) {
            return DataResponse.fail("订单不存在");
        }
        try {
            Order order = orderMapper.selectById(orderId);
            order.setNote(MapUtil.getString(params, "note"));
            order.setUpdateUser(userId);
            order.setUpdateTime(time);
            orderMapper.updateById(order);
            // 记录日志
            dealOperateLog(userId, time, order, "", "note", "");
        } catch (Exception e) {
            e.printStackTrace();
            return DataResponse.fail("订单后台备注失败");
        }
        return DataResponse.success();
    }


    @Override
    public void dealOperateLog(Long userId, String time, Order order, String note, String type, String optName) {
        User user = userService.getById(userId);
        if (user == null) {
            user = new User();
            if("rabbit".equals(optName)) {
                user.setUserName("RABBITMQ消息队列");
            } else {
                user.setUserName("后台管理员");
            }

        }
        String remark = "订单";
        OrderOperateLog orderOperateLog = new OrderOperateLog();
        if ("close".equals(type)) {
            remark = remark + "关闭：" + note;
        } else if ("delete".equals(type)) {
            remark = remark + "删除";
        } else if ("delivery".equals(type)) {
            remark = remark + "发货";
        } else if ("money".equals(type)) {
            remark = remark + "修改价格";
        } else if ("receiver".equals(type)) {
            remark = remark + "修改收货人信息";
        } else if ("note".equals(type)) {
            remark = remark + "修改备注信息";
        } else if ("cancel".equals(type)) {
            remark = remark + note;
        }
        orderOperateLog.setId(SnowflakeIdWorker.generateId());
        orderOperateLog.setOrderId(order.getId());
        orderOperateLog.setOrderStatus(order.getOrderStatus());
        orderOperateLog.setRemark(remark);
        orderOperateLog.setIsDelete(0);
        orderOperateLog.setAddUser(userId);
        orderOperateLog.setAddTime(time);
        orderOperateLog.setUpdateUser(userId);
        orderOperateLog.setUpdateTime(time);
        orderOperateLogService.save(orderOperateLog);
    }

    @Override
    public void updateOrder(Order order, Transaction transaction) {
        LOGGER.info("交易支付订单Order: "+order);
        order.setPayAmount(transaction.getAmount());
        order.setPayChannel(transaction.getPayChannel());
        order.setPayTime(transaction.getTransactionTime());
        order.setOrderStatus(OrderStatusDict.PAY.getCode());
        orderMapper.updateById(order);
        // 库存扣减
        QueryWrapper<OrderDetail> orderDetailQueryWrapper = new QueryWrapper<>();
        orderDetailQueryWrapper.eq("ORDER_ID", order.getId());
        orderDetailQueryWrapper.eq("IS_DELETE", 0);
        List<OrderDetail> list = orderDetailService.list(orderDetailQueryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            for (OrderDetail orderDetail:list) {
                Sku sku = skuService.getById(orderDetail.getSkuId());
                if (sku != null) {
                    int exStock = sku.getSkuStock() - orderDetail.getQuantity();
                    sku.setSkuStock(exStock);
                    skuService.updateById(sku);
                }
                Goods goods = goodsService.getById(orderDetail.getGoodsId());
                if (goods != null) {
                    int exStock = goods.getStock() - orderDetail.getQuantity();
                    goods.setStock(exStock);
                    goodsService.updateById(goods);
                }
            }
        }
    }

    @Override
    public DataResponse updateOrderStatus(Map<String, Object> params) {
        String transactionId = MapUtil.getMapValue(params, "transactionId");
        Long orderId = MapUtil.getLong(params, "orderId");
        String type = MapUtil.getString(params, "type");
        QueryWrapper<Transaction> entityWrapper = new QueryWrapper<>();
        entityWrapper.eq("IS_DELETE", 0);
        entityWrapper.eq("ID", transactionId);
        Transaction transaction = transactionService.getOne(entityWrapper);
        if(transaction != null){
            transaction.setStatus("SW1202");
            transactionService.updateById(transaction);
            // 更新订单状态
            if("order".equals(type)){
                Order order = orderMapper.selectById(orderId);
                updateOrder(order, transaction);
            }
        }
        return DataResponse.success();
    }

}
