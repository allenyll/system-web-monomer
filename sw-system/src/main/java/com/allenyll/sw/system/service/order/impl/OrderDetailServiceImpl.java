package com.allenyll.sw.system.service.order.impl;

import com.allenyll.sw.system.base.IUserService;
import com.allenyll.sw.system.mapper.order.OrderDetailMapper;
import com.allenyll.sw.system.service.member.impl.CustomerAddressServiceImpl;
import com.allenyll.sw.system.service.member.impl.CustomerServiceImpl;
import com.allenyll.sw.system.service.order.IOrderDetailService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.common.entity.customer.Customer;
import com.allenyll.sw.common.entity.customer.CustomerAddress;
import com.allenyll.sw.common.entity.order.Order;
import com.allenyll.sw.common.entity.order.OrderDetail;
import com.allenyll.sw.common.entity.order.OrderOperateLog;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.CollectionUtil;
import com.allenyll.sw.common.util.DateUtil;
import com.allenyll.sw.common.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单明细表
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-06-26 21:15:58
 */
@Service("orderDetailService")
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements IOrderDetailService {

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Autowired
    OrderServiceImpl orderService;

    @Autowired
    CustomerServiceImpl customerService;

    @Autowired
    CustomerAddressServiceImpl customerAddressService;

    @Autowired
    OrderOperateLogServiceImpl orderOperateLogService;

    @Autowired
    IUserService userService;

    public List<OrderDetail> getOrderDetailList(Map<String, Object> params){
        QueryWrapper<OrderDetail> wrapper  = new QueryWrapper<>();
        wrapper.eq("IS_DELETE", 0);
        wrapper.eq("ORDER_ID", MapUtil.getString(params, "orderId"));
        List<OrderDetail> list = orderDetailMapper.selectList(wrapper);
        return list;
    }

    public Order getOrderDetail(Map<String, Object> map) {
        QueryWrapper<Order> wrapper  = new QueryWrapper<>();
        wrapper.eq("IS_DELETE", 0);
        wrapper.eq("ID", MapUtil.getString(map, "orderId"));
        Order order = orderService.getOne(wrapper);
        if(order == null){
            return null;
        }
        String addTime = order.getOrderTime();
        Date addDate = DateUtil.stringToDate(addTime, "yyyy-MM-dd HH:mm:ss");
        Date nowDate = new Date();
        long unPayTime = addDate.getTime() + 30 * 60 * 1000 - nowDate.getTime() ;
        order.setUnPayTime(unPayTime);
        Customer customer = customerService.getById(order.getCustomerId());
        order.setCustomer(customer);
        CustomerAddress customerAddress = customerAddressService.getById(order.getAddressId());
        order.setCustomerAddress(customerAddress);
        List<OrderDetail> orderDetailList = getOrderDetailList(map);
        order.setOrderDetails(orderDetailList);
        List<OrderOperateLog> orderOperateLogs = orderOperateLogService.getOperateList(map);
        if (CollectionUtil.isNotEmpty(orderOperateLogs)) {
            for (OrderOperateLog log:orderOperateLogs) {
                User user = userService.getById(log.getUpdateUser());
                if (user == null) {
                    Customer cus = customerService.getById(log.getUpdateUser());
                    if (cus != null) {
                        log.setOptUserName(cus.getCustomerName());
                    }
                } else {
                    log.setOptUserName(user.getUserName());
                }
            }
        }
        order.setOrderOperateLogs(orderOperateLogs);
        return order;
    }

}
