package com.allenyll.sw.system.service.member.impl;

import com.allenyll.sw.common.entity.customer.CustomerPointDetail;
import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.common.util.MapUtil;
import com.allenyll.sw.system.mapper.member.CustomerPointDetailMapper;
import com.allenyll.sw.system.mapper.member.CustomerPointMapper;
import com.allenyll.sw.system.service.member.ICustomerPointService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.common.entity.customer.CustomerPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yu.leilei
 * @since 2019-01-09
 */
@Service("customerPointService")
public class CustomerPointServiceImpl extends ServiceImpl<CustomerPointMapper, CustomerPoint> implements ICustomerPointService {

    @Autowired
    private CustomerPointDetailMapper customerPointDetailMapper;

    @Autowired
    private CustomerPointMapper customerPointMapper;
    
    @Override
    public Map<String, Object> getPointDetail(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();
        Long customerId = MapUtil.getLong(param, "customerId");
        String action = MapUtil.getMapValue(param, "action");
        String pageStr = MapUtil.getMapValue(param, "page");

        Integer page = Integer.parseInt(pageStr);

        QueryWrapper<CustomerPointDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("CUSTOMER_ID", customerId);
        wrapper.eq("TYPE", action);
        wrapper.eq("IS_DELETE", 0);
        Page<CustomerPointDetail> pageList = new Page<>(page, 10);
        Page<CustomerPointDetail> list = customerPointDetailMapper.selectPage(pageList, wrapper);

        long isMore = list.getSize();

        if(isMore < 10){
            result.put("is_more", 0);
        }else {
            result.put("is_more", 1);
        }
        result.put("current_page", page);

        result.put("list", list);

        return result;
    }

    @Override
    public Map<String, Object> getPoint(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();

        String customerId = MapUtil.getMapValue(param, "customerId");

        QueryWrapper<CustomerPoint> wrapper = new QueryWrapper<>();
        wrapper.eq("CUSTOMER_ID", customerId);

        CustomerPoint customerPoint = customerPointMapper.selectOne(wrapper);
        if (customerPoint == null) {
            customerPoint = new CustomerPoint();
            customerPoint.setPoint(0);
        }
        result.put("customerPoint", customerPoint);

        return result;
    }
}
