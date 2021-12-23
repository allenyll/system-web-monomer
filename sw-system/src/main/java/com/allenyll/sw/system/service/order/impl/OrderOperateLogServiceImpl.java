package com.allenyll.sw.system.service.order.impl;

import com.allenyll.sw.system.mapper.order.OrderOperateLogMapper;
import com.allenyll.sw.system.service.order.IOrderOperateLogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.common.entity.order.OrderOperateLog;
import com.allenyll.sw.common.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 记录订单操作日志
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-07-16 16:34:06
 */
@Service("orderOperateLogService")
public class OrderOperateLogServiceImpl extends ServiceImpl<OrderOperateLogMapper, OrderOperateLog> implements IOrderOperateLogService {

    @Autowired
    OrderOperateLogMapper orderOperateLogMapper;

    public List<OrderOperateLog> getOperateList(Map<String, Object> map) {
        QueryWrapper<OrderOperateLog> wrapper  = new QueryWrapper<>();
        wrapper.eq("is_delete", 0);
        wrapper.eq("ORDER_ID", MapUtil.getLong(map, "orderId"));
        return  orderOperateLogMapper.selectList(wrapper);
    }
}
