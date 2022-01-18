package com.allenyll.sw.system.service.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.allenyll.sw.common.dto.OrderQueryDto;
import com.allenyll.sw.common.dto.OrderReturnDto;
import com.allenyll.sw.common.entity.order.Order;
import com.allenyll.sw.common.entity.pay.Transaction;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.common.util.Result;

import java.util.List;
import java.util.Map;

public interface IOrderService extends IService<Order> {

    /**
     * 获取订单数量
     * @param params
     * @return
     */
    int selectCount(Map<String, Object> params);

    /**
     * 分页查询订单
     * @param params
     * @return
     */
    List<Map<String, Object>> getOrderPage(Map<String, Object> params);

    /**
     * 删除订单
     * @param params
     * @return
     */
    DataResponse deleteOrder(Map<String, Object> params);

    /**
     * 订单发货
     * @param params
     * @return
     */
    DataResponse deliveryOrder(Map<String, Object> params);

    /**
     * 关闭订单
     * @param params
     * @return
     */
    DataResponse closeOrder(Map<String, Object> params);

    /**
     * 修改订单价格
     * @param params
     * @return
     */
    DataResponse updateMoneyInfo(Map<String, Object> params);

    /**
     * 修改订单收货人信息
     * @param params
     * @return
     */
    DataResponse updateReceiverInfo(Map<String, Object> params);

    /**
     * 修改订单备注
     * @param params
     * @return
     */
    DataResponse updateOrderNote(Map<String, Object> params);

    /**
     * 创建订单
     * @param order
     * @param param
     */
    void createOrder(Order order, Map<String, Object> param);

    /**
     * 获取未支付订单数量
     * @param params
     * @return
     */
    int getUnPayNum(Map<String, Object> params);

    /**
     * 获取未收货订单数量
     * @param params
     * @return
     */
    int getUnReceiveNum(Map<String, Object> params);

    /**
     * 获取已发货订单数量
     * @param params
     * @return
     */
    int getDeliveryNum(Map<String, Object> params);

    /**
     * 获取已收货订单数量
     * @param params
     * @return
     */
    int getReceiveNum(Map<String, Object> params);

    /**
     * 获取未评价订单数量
     * @param params
     * @return
     */
    int getAppraisesNum(Map<String, Object> params);

    /**
     * 获取已完成订单数量
     * @param params
     * @return
     */
    int getFinishNum(Map<String, Object> params);

    /**
     * 获取订单列表
     * @param params
     * @return
     */
    List<Order> getOrderList(OrderQueryDto queryDto);

    /**
     * 取消订单
     * @param map
     */
    DataResponse cancelOrder(Map<String, Object> map);

    /**
     * 将未支付订单放置消息队列中
     * @param params
     * @return
     */
    DataResponse sendMessage(Map<String, Object> params);

    /**
     * 获取订单信息
     * @param user
     * @param queryDto
     * @return
     */
    Result<OrderReturnDto> getOrderInfo(User user, OrderQueryDto queryDto);

    /**
     * 记录订单操作日志
     * @param id 操作人ID
     * @param time 操作时间
     * @param order 订单信息
     * @param note 操作备注
     * @param type 操作类型
     * @param optName 操作名称
     */
    void dealOperateLog(Long id, String time, Order order, String note, String type, String optName);

    /**
     * 更新订单信息，扣减库存
     * @param order 参数
     * @param transaction 支付信息
     */
    void updateOrder(Order order, Transaction transaction);

    /**
     * 支付完成更新订单
     * @param params
     * @return
     */
    DataResponse updateOrderStatus(Map<String, Object> params);
}
