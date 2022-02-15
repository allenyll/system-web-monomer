package com.allenyll.sw.sso.service;

import com.allenyll.sw.common.entity.auth.AuthToken;
import com.allenyll.sw.common.entity.customer.Customer;
import com.allenyll.sw.common.util.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @Author:       allenyll
 * @Date:         2020/9/10 8:12 下午
 * @Version:      1.0
 */
public interface IWxUserService extends IService<Customer> {

    /**
     * 微信小程序登陆
     * @param code 授权码
     * @param mode 所属小程序
     * @return
     */
    AuthToken token(HttpServletRequest request, String code, String mode, Customer customer);

    /**
     * 根据openid 查询用户
     * @param openid
     * @return
     */
    Result<Customer> queryUserByOpenId(String openid, String mode);

    /**
     * 根据客户名称
     * @param username
     * @return
     */
    Customer selectUserByName(String username);
}
