package com.allenyll.sw.system.service.member;

import com.allenyll.sw.common.util.DataResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.allenyll.sw.common.entity.customer.CustomerBalance;

import java.util.Map;

public interface ICustomerBalanceService extends IService<CustomerBalance> {

    /**
     * 更新余额
     * @param params
     * @return
     */
    DataResponse updateBalance(Map<String, Object> params);

    /**
     * 获取余额
     * @param param
     * @return
     */
    DataResponse getBalance(Map<String, Object> param);
}
