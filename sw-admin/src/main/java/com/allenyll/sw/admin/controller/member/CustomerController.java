package com.allenyll.sw.admin.controller.member;


import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.system.service.member.impl.CustomerServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.allenyll.sw.common.constants.BaseConstants;
import com.allenyll.sw.common.dto.CustomerQueryDto;
import com.allenyll.sw.common.dto.CustomerResult;
import com.allenyll.sw.common.entity.customer.Customer;
import com.allenyll.sw.common.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author yu.leilei
 * @since 2018-10-22
 */
@Api(value = "用户接口", tags = "小程序用户管理")
@Slf4j
@RestController
@RequestMapping("/customer")
public class CustomerController extends BaseController<CustomerServiceImpl, Customer> {

    @ApiOperation("查询用户")
    @ResponseBody
    @RequestMapping(value = "/selectOne", method = RequestMethod.POST)
    public Customer selectOne(@RequestBody Map<String, Object> map) {
        String mark = MapUtil.getString(map, "MARK");
        QueryWrapper<Customer> customerEntityWrapper = new QueryWrapper<>();
        customerEntityWrapper.eq("IS_DELETE", 0);
        if (BaseConstants.SW_WECHAT.equals(mark)) {
            customerEntityWrapper.eq("OPENID", MapUtil.getString(map, "OPENID"));
        } else {
            customerEntityWrapper.eq("id", MapUtil.getLong(map, "CUSTOMER_ID"));
        }
        return service.getOne(customerEntityWrapper);
    }

    @ApiOperation("根据customerId查询用户")
    @ResponseBody
    @RequestMapping(value = "/selectCustomerById", method = RequestMethod.POST)
    public Customer selectCustomerById(@RequestParam Long fkCustomerId) {
        return service.getById(fkCustomerId);
    }

    @ApiOperation("更新用户信息")
    @RequestMapping(value = "/updateById", method = RequestMethod.POST)
    public void updateById(@RequestBody Customer customer) {
        service.updateById(customer);
    }

    @ApiOperation("[小程序接口]登录/注册用户")
    @RequestMapping(value = "/loginOrRegisterConsumer", method = RequestMethod.POST)
    public void loginOrRegisterConsumer(@RequestBody Customer customer) {
        service.loginOrRegisterConsumer(customer);
    }

    @ApiOperation("根据用户名查询用户")
    @RequestMapping(value = "selectUserByName", method = RequestMethod.POST)
    public Customer selectUserByName(@RequestParam String userName) {
        return service.selectUserByName(userName);
    }

    @ApiOperation(value = "获取客户列表")
    @ResponseBody
    @RequestMapping(value = "getCustomerList", method = RequestMethod.POST)
    public Result<List<Customer>> getCustomerList(@RequestBody CustomerQueryDto customerQueryDto ) {
        return service.getCustomerList(customerQueryDto);
    }

    @ApiOperation(value = "分页获取客户列表")
    @ResponseBody
    @RequestMapping(value = "getCustomerPage", method = RequestMethod.POST)
    public Result<CustomerResult> getCustomerPage(@RequestBody CustomerQueryDto customerQueryDto ) {
        return service.getCustomerPage(customerQueryDto);
    }
    
    @ResponseBody
    @ApiOperation(value = "[小程序接口]更新用户")
    @RequestMapping(value = "/updateCustomer", method = RequestMethod.POST)
    public Result<Customer> updateCustomer(@RequestBody Customer customer){
        Result<Customer> result = service.updateCustomer(customer);
        return result;
    }

    @ApiOperation(value = "[小程序接口]更新用户")
    @RequestMapping(value = "/updateCustomerAccount", method = RequestMethod.POST)
    public Result<Customer> updateCustomerAccount(@RequestBody Map<String, Object> params){
        return service.updateCustomerAccount(params);
    }

    @ApiOperation(value = "[小程序接口]根据openid查询用户")
    @ResponseBody
    @RequestMapping(value = "queryUserByOpenId", method = RequestMethod.POST)
    public Result<Customer>  queryUserByOpenId(@RequestParam String openid){
        log.info("开始调用查询微信用户openid:" + openid);
        Result<Customer> result = service.queryUserByOpenId(openid);
        return result;
    }

    @ApiOperation(value = "[小程序接口]获取微信用户手机号，并更新到数据库")
    @ResponseBody
    @RequestMapping(value = "/getPhoneNumber", method = RequestMethod.POST)
    public Result<Customer> getPhoneNumber(@RequestBody Map<String, Object> params){
        return service.getPhoneNumber(params);
    }
}
