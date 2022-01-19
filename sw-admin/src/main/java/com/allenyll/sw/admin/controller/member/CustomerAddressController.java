package com.allenyll.sw.admin.controller.member;

import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.common.exception.BusinessException;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.system.service.member.impl.CustomerAddressServiceImpl;
import com.allenyll.sw.common.entity.customer.CustomerAddress;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Description:  地址管理接口
 * @Author:       allenyll
 * @Date:         2020/5/26 10:21 下午
 * @Version:      1.0
 */
@Api("地址管理通用接口")
@Slf4j
@RestController
@RequestMapping("/customerAddress")
public class CustomerAddressController extends BaseController<CustomerAddressServiceImpl, CustomerAddress> {

    @ApiOperation("[小程序接口]获取地址详情")
    @ResponseBody
    @RequestMapping(value = "/getAddress/{id}", method = RequestMethod.GET)
    public Result getAddress(@PathVariable Long id){
        Result result = new Result();
        CustomerAddress customerAddress = service.getById(id);
        result.setData(customerAddress);
        return result;
    }

    @ApiOperation("[小程序接口]设置地址")
    @ResponseBody
    @RequestMapping(value = "/setAddress", method = RequestMethod.POST)
    public Result setAddress(@CurrentUser(isFull = true) User user, @RequestBody Map<String, Object> params){
        return service.setAddress(user, params);
    }

    @ApiOperation("[小程序接口]获取地址列表")
    @ResponseBody
    @RequestMapping(value = "/getAddressList", method = RequestMethod.POST)
    public Result<List<CustomerAddress>> getAddressList(@RequestBody Map<String, Object> params){
        return service.getAddressList(params);
    }

    @ApiOperation("[小程序接口]根据id删除地址")
    @ResponseBody
    @RequestMapping(value = "/deleteAddress",method = RequestMethod.POST)
    public Result deleteAddress(@CurrentUser(isFull = true) User user, @RequestBody Map<String, Object> params){
        return service.deleteAddress(user, params);
    }

    @ApiOperation("[小程序接口]根据ID更新地址")
    @ResponseBody
    @RequestMapping(value = "/updateAddress/{id}", method = RequestMethod.POST)
    public Result updateAddress(@PathVariable String id){
        Result result = new Result();
        try {
            service.updateAddress(id);
        } catch (BusinessException e) {
            result.fail(e.getMessage());
            return result;
        }
        return result;
    }

    @ApiOperation("[小程序接口]根据id选择地址")
    @RequestMapping(value = "/selectAddressById", method = RequestMethod.POST)
    public CustomerAddress selectAddressById(@RequestParam String fkAddressId) {
        return service.getById(fkAddressId);
    }

}
