package com.allenyll.sw.system.service.member;

import com.allenyll.sw.common.util.DataResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.allenyll.sw.common.entity.customer.CustomerPoint;

import java.util.Map;

public interface ICustomerPointService extends IService<CustomerPoint> {

    /**
     * 获取积分详情
     * @param param
     * @return
     */
    Map<String, Object> getPointDetail(Map<String, Object> param);

    /**
     * 获取积分
     * @param param
     * @return
     */
    Map<String, Object> getPoint(Map<String, Object> param);
}
