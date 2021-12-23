package com.allenyll.sw.system.service.member.impl;

import com.allenyll.sw.common.entity.customer.CustomerBalanceDetail;
import com.allenyll.sw.common.enums.dict.StatusDict;
import com.allenyll.sw.common.util.*;
import com.allenyll.sw.system.mapper.member.CustomerBalanceDetailMapper;
import com.allenyll.sw.system.mapper.member.CustomerBalanceMapper;
import com.allenyll.sw.system.service.member.ICustomerBalanceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.common.entity.customer.CustomerBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 会员余额表
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-04-10 16:16:16
 */
@Service("customerBalanceService")
public class CustomerBalanceServiceImpl extends ServiceImpl<CustomerBalanceMapper, CustomerBalance> implements ICustomerBalanceService {

    @Autowired
    private CustomerBalanceMapper customerBalanceMapper;
    
    @Autowired
    private CustomerBalanceDetailMapper customerBalanceDetailMapper;
    
    @Override
    public DataResponse updateBalance(Map<String, Object> params) {
        String openid = MapUtil.getMapValue(params, "openid");
        if(openid.equals(AppContext.getCurrentUserWechatOpenId())){
            String amountStr = MapUtil.getMapValue(params, "amount", "0");
            Long customerId = MapUtil.getLong(params, "customerId");
            String remark = MapUtil.getMapValue(params, "remark", "");
            BigDecimal amount = new BigDecimal(amountStr);
            if(StringUtil.isEmpty(openid)){
                return DataResponse.fail("用户不能为空");
            }

            QueryWrapper<CustomerBalance> customerBalanceEntityWrapper = new QueryWrapper<>();
            customerBalanceEntityWrapper.eq("IS_DELETE", 0);
            customerBalanceEntityWrapper.eq("CUSTOMER_ID", customerId);
            CustomerBalance customerBalance = customerBalanceMapper.selectOne(customerBalanceEntityWrapper);
            if(customerBalance == null){
                // 新增
                customerBalance = new CustomerBalance();
                customerBalance.setBalance(amount);
                customerBalance.setWithdrawCash(new BigDecimal("0"));
                customerBalance.setCustomerId(customerId);
                customerBalance.setIsDelete(0);
                customerBalance.setAddUser(customerId);
                customerBalance.setAddTime(DateUtil.getCurrentDateTime());
                customerBalance.setUpdateUser(customerId);
                customerBalance.setUpdateTime(DateUtil.getCurrentDateTime());
                customerBalanceMapper.insert(customerBalance);
            }else{
                BigDecimal balance = customerBalance.getBalance();
                balance = balance.add(amount);
                customerBalance.setBalance(balance);
                customerBalance.setUpdateUser(customerId);
                customerBalance.setUpdateTime(DateUtil.getCurrentDateTime());
                customerBalanceMapper.updateById(customerBalance);
            }

            // 新增余额明细
            CustomerBalanceDetail customerBalanceDetail = new CustomerBalanceDetail();
            customerBalanceDetail.setCustomerId(customerId);
            customerBalanceDetail.setBalance(amount);
            customerBalanceDetail.setType("SW1501");
            customerBalanceDetail.setRemark(remark);
            customerBalanceDetail.setStatus(StatusDict.START.getCode());
            customerBalanceDetail.setTime(DateUtil.getCurrentDateTime());
            customerBalanceDetail.setIsDelete(0);
            customerBalanceDetail.setAddUser(customerId);
            customerBalanceDetail.setAddTime(DateUtil.getCurrentDateTime());
            customerBalanceDetail.setUpdateUser(customerId);
            customerBalanceDetail.setUpdateTime(DateUtil.getCurrentDateTime());
            customerBalanceDetailMapper.insert(customerBalanceDetail);
        }else{
            DataResponse.fail("当前登录用户不匹配");
        }
        return DataResponse.success();
    }

    @Override
    public DataResponse getBalance(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();

        String customerId = MapUtil.getMapValue(param, "customerId");

        QueryWrapper<CustomerBalance> wrapper = new QueryWrapper<>();
        wrapper.eq("CUSTOMER_ID", customerId);

        CustomerBalance customerBalance = customerBalanceMapper.selectOne(wrapper);

        if (customerBalance == null) {
            customerBalance = new CustomerBalance();
            customerBalance.setBalance(new BigDecimal(0));
        }

        result.put("customerBalance", customerBalance);

        return DataResponse.success(result);
    }
}
