package com.allenyll.sw.system.service.member.impl;

import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.common.util.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.system.mapper.member.CustomerBalanceDetailMapper;
import com.allenyll.sw.system.service.member.ICustomerBalanceDetailService;
import com.allenyll.sw.common.entity.customer.CustomerBalanceDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 会员余额明细表
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-04-10 16:24:29
 */
@Slf4j
@Service("customerBalanceDetailService")
public class CustomerBalanceDetailServiceImpl extends ServiceImpl<CustomerBalanceDetailMapper, CustomerBalanceDetail> implements ICustomerBalanceDetailService {

    @Autowired
    private CustomerBalanceDetailMapper customerBalanceDetailMapper;
    
    @Override
    public Map<String, Object> getBalanceDetail(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();

        Long customerId = MapUtil.getLong(param, "customerId");
        String action = MapUtil.getMapValue(param, "action");
        String pageStr = MapUtil.getMapValue(param, "page");

        Integer page = Integer.parseInt(pageStr);

        QueryWrapper<CustomerBalanceDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("CUSTOMER_ID", customerId);
        if(!"SW0500".equals(action)){
            wrapper.eq("TYPE", action);
        }
        wrapper.eq("IS_DELETE", 0);
        Page<CustomerBalanceDetail> pageList = new Page<>(page, 10);
        Page<CustomerBalanceDetail> list = customerBalanceDetailMapper.selectPage(pageList, wrapper);

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
}
