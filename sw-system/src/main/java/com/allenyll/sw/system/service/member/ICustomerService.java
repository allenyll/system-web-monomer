package com.allenyll.sw.system.service.member;

import com.baomidou.mybatisplus.extension.service.IService;
import com.allenyll.sw.common.dto.CustomerQueryDto;
import com.allenyll.sw.common.dto.CustomerResult;
import com.allenyll.sw.common.entity.customer.Customer;
import com.allenyll.sw.common.util.Result;

import java.util.List;
import java.util.Map;

public interface ICustomerService extends IService<Customer> {

    /**
     * 根据客户名称获取客户
     * @param userName
     * @return
     */
    Customer selectUserByName(String userName);

    /**
     * 更新用户
     * @param customer
     * @return
     */
    Result<Customer> updateCustomer(Customer customer);

    /**
     * 根据openid查询一次用户
     * @param openid
     * @return
     */
    Result<Customer> queryUserByOpenId(String openid);

    /**
     * 获取微信用户手机号，并更新到数据库
     * @param params
     * @return
     */
    Result<Customer> getPhoneNumber(Map<String, Object> params);

    /**
     * 用户更新账户信息
     * @param params
     * @return
     */
    Result<Customer> updateCustomerAccount(Map<String, Object> params);

    /**
     * 获取用户列表
     * @param customerQueryDto
     * @return
     */
    Result<List<Customer>> getCustomerList(CustomerQueryDto customerQueryDto);

    /**
     * 分页获取用户列表
     * @param customerQueryDto
     * @return
     */
    Result<CustomerResult> getCustomerPage(CustomerQueryDto customerQueryDto);
    
}
