package com.allenyll.sw.admin.controller.system;


import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.common.annotation.Log;
import com.allenyll.sw.common.enums.dict.StatusDict;
import com.allenyll.sw.system.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.allenyll.sw.system.base.impl.DictServiceImpl;
import com.allenyll.sw.common.entity.system.Dict;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.common.util.SnowflakeIdWorker;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字典表
 前端控制器
 * </p>
 *
 * @author yu.leilei
 * @since 2019-01-29
 */
@Api(value = "字典管理相关接口", tags = "菜单管理")
@RestController
@RequestMapping("dict")
public class DictController extends BaseController<DictServiceImpl, Dict> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictController.class);

    @Autowired
    DictServiceImpl dictService;

    @Override
    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @Log("添加字典数据")
    public DataResponse add(@CurrentUser(isFull = true) User user, @RequestBody Dict dict) {
        dict.setId(SnowflakeIdWorker.generateId());
        return super.add(user, dict);
    }

    @ApiOperation("字典列表")
    @RequestMapping(value = "list/{code}", method = RequestMethod.POST)
    public DataResponse list(@PathVariable String code) {
        return service.getDictList(code);
    }

    @ApiOperation(value = "获取 parentId = 0 的字典集合")
    @GetMapping("/getParent")
    public DataResponse getParent(){
        return service.getParent();
    }


    @ApiOperation(value = "获取子字典集合")
    @RequestMapping(value = "/getChild/{id}", method = RequestMethod.GET)
    public DataResponse getChild(@PathVariable String id){
        return service.getChild(id);
    }

    @ApiOperation("根据字典code获取具体描述")
    @RequestMapping(value = "getDictByCode", method = RequestMethod.POST)
    public Dict getDictByCode(@RequestParam String orderStatus) {
        return service.getDictByCode(orderStatus);
    }

}
