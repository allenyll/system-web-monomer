package com.allenyll.sw.admin.controller.member;

import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.common.entity.customer.CustomerLevel;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.common.util.SnowflakeIdWorker;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.system.service.member.impl.CustomerLevelServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@Api(tags = "会员等级管理")
@RestController
@RequestMapping("customerLevel")
public class CustomerLevelController extends BaseController<CustomerLevelServiceImpl, CustomerLevel> {

    @Override
    @ResponseBody
    @ApiOperation("添加会员等级")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public DataResponse add(@CurrentUser(isFull = true) User user, @RequestBody CustomerLevel entity) {
        entity.setId(SnowflakeIdWorker.generateId());
        return super.add(user, entity);
    }

}
