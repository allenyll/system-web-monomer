package com.allenyll.sw.system.service.member;

import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import com.allenyll.sw.common.entity.customer.CustomerAddress;

import java.util.List;
import java.util.Map;

public interface ICustomerAddressService extends IService<CustomerAddress> {

    /**
     * 微信小程序地址管理
     * @param user
     * @param params
     * @return
     */
    Result setAddress(User user, Map<String, Object> params);

    /**
     * 获取地址列表
     * @param params
     * @return
     */
    Result<List<CustomerAddress>> getAddressList(Map<String, Object> params);

    /**
     * 根据id删除地址
     * @param user
     * @param params
     * @return
     */
    Result deleteAddress(User user, Map<String, Object> params);

    /**
     * 根据ID更新地址
     * @param id
     * @return
     */
    void updateAddress(String id);
}
