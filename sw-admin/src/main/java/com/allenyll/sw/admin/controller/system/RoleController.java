package com.allenyll.sw.admin.controller.system;


import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.common.annotation.Log;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.common.entity.system.*;
import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.common.util.SnowflakeIdWorker;
import com.allenyll.sw.system.base.impl.RoleMenuServiceImpl;
import com.allenyll.sw.system.base.impl.RoleServiceImpl;
import com.allenyll.sw.system.base.impl.UserRoleServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
@Api(value = "角色管理相关接口", tags = "角色管理")
@Controller
@RequestMapping("role")
public class RoleController  extends BaseController<RoleServiceImpl, Role> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    RoleMenuServiceImpl roleMenuService;

    @Autowired
    UserRoleServiceImpl userRoleService;

    @Override
    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public DataResponse add(@CurrentUser(isFull = true) User user, @RequestBody Role role) {
        role.setId(SnowflakeIdWorker.generateId());
        return super.add(user, role);
    }

    @ApiOperation("获取角色列表")
    @ResponseBody
    @RequestMapping(value = "getRoleList/{id}", method = RequestMethod.GET)
    public DataResponse getAllRole(@PathVariable String id){
        return service.getAllRole(id);
    }

    @ApiOperation("配置角色菜单")
    @RequestMapping(value = "/setMenus",method = RequestMethod.POST)
    @ResponseBody
    @Log("配置角色菜单")
    public DataResponse setMenus(@RequestBody Map<String, Object> params){
        return service.setMenus(params);
    }

    @ApiOperation("获取角色授权菜单")
    @ResponseBody
    @RequestMapping(value = "getRoleMenus/{id}", method = RequestMethod.GET)
    public DataResponse getRoleMenus(@PathVariable String id){
        return service.getRoleMenus(id);
    }

}
