package com.allenyll.sw.admin.controller.member;

import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.system.service.member.impl.CustomerBalanceDetailServiceImpl;
import com.allenyll.sw.system.service.member.impl.CustomerBalanceServiceImpl;
import com.allenyll.sw.system.service.member.impl.CustomerServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.allenyll.sw.common.entity.customer.CustomerBalance;
import com.allenyll.sw.common.util.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("customerBalance")
public class CustomerBalanceController extends BaseController<CustomerBalanceServiceImpl, CustomerBalance> {

    @Autowired
    CustomerServiceImpl customerService;

    @Autowired
    CustomerBalanceServiceImpl customerBalanceService;

    @Autowired
    CustomerBalanceDetailServiceImpl customerBalanceDetailService;

    @ApiOperation("更新活余额")
    @RequestMapping(value = "/updateBalance",method = RequestMethod.POST)
    @ResponseBody
    public DataResponse updateObj(@RequestBody Map<String, Object> params){
        return service.updateBalance(params);
    }

    @ApiOperation("获取余额")
    @ResponseBody
    @RequestMapping(value = "/getBalance", method = RequestMethod.POST)
    public DataResponse getBalance(@RequestBody Map<String, Object> param){
        log.info("==============开始调用 getBalance================");
        return service.getBalance(param);
    }

    @ResponseBody
    @RequestMapping(value = "/selectOne", method = RequestMethod.POST)
    public CustomerBalance selectOne(@RequestBody Map<String, Object> map) {
        QueryWrapper<CustomerBalance> wrapper = new QueryWrapper<>();
        wrapper.eq("IS_DELETE", 0);
        wrapper.eq("CUSTOMER_ID", MapUtil.getLong(map, "CUSTOMER_ID"));
        return service.getOne(wrapper);
    }
}
