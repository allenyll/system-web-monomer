package com.allenyll.sw.system.service.member;

import com.allenyll.sw.common.util.DataResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.allenyll.sw.common.entity.customer.CustomerBalanceDetail;

import java.util.Map;

public interface ICustomerBalanceDetailService extends IService<CustomerBalanceDetail> {

    /**
     * 获取微信余额明细
     * @param param
     * @return
     */
    Map<String, Object> getBalanceDetail(Map<String, Object> param);
}
