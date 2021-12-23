package com.allenyll.sw.admin.controller.system;

import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.common.annotation.Log;
import com.allenyll.sw.system.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.allenyll.sw.common.entity.system.Depot;
import com.allenyll.sw.common.entity.system.SysUserRole;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.*;
import com.allenyll.sw.system.base.impl.DepotServiceImpl;
import com.allenyll.sw.system.base.impl.UserRoleServiceImpl;
import com.allenyll.sw.system.base.impl.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Description:  用户管理控制器
 * @Author:       allenyll
 * @Date:         2020/5/10 11:13 下午
 * @Version:      1.0
 */
@Slf4j
@Api(value = "用户管理相关操作", tags = "用户管理")
@RestController
@RequestMapping("user")
public class UserController extends BaseController<UserServiceImpl, User> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Resource
    UserServiceImpl userService;

    @Autowired
    UserRoleServiceImpl userRoleService;

    @Autowired
    DepotServiceImpl depotService;

    @ResponseBody
    @ApiOperation(value = "根据token获取当前用户所属角色拥有的菜单", notes = "根据token获取当前用户所属角色拥有的菜单")
    @RequestMapping(value = "getUserInfo", method = RequestMethod.GET)
    public Result getUserInfo(@RequestParam String token){
        return userService.getUserInfo(token);
    }

    @ApiOperation("测试获取当前用户")
    @RequestMapping(value = "getCurrentUser", method = RequestMethod.POST)
    public DataResponse getCurrentUser(@CurrentUser(isFull = true) User user) {
        log.info("当前用户：{}", user.getAccount());
        return DataResponse.success();
    }

    /**
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "根据账户获取用户")
    @RequestMapping(value = "/selectOne", method = RequestMethod.POST)
    public User selectOne(@RequestBody Map<String, Object> params) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("account", MapUtil.getString(params, "account"));
        wrapper.eq("status", MapUtil.getString(params, "status"));
        wrapper.eq("is_delete", MapUtil.getString(params, "is_delete"));
        return userService.getOne(wrapper);
    }

    /**
     * 获取用户权限
     * @param params
     * @return
     */
    @ApiOperation(value = "获取用户权限")
    @RequestMapping(value = "/selectOneSysUserRole", method = RequestMethod.POST)
    public SysUserRole selectOneSysUserRole(@RequestBody Map<String, Object> params) {
        QueryWrapper<SysUserRole> wrapper = new QueryWrapper<>();
        String userId = MapUtil.getString(params, "user_id");
        wrapper.eq("user_id", userId);
        return userRoleService.getOne(wrapper);
    }

    /**
     * 根据用户信息获取菜单
     * @param params
     * @return
     */
    @ApiOperation(value = "根据用户信息获取菜单")
    @RequestMapping(value = "/getUserRoleMenuList", method = RequestMethod.POST)
    public List<Map<String, Object>> getUserRoleMenuList(@RequestBody Map<String, Object> params) {
        return userService.getUserRoleMenuList(params);
    }

    @Override
    @ApiOperation(value = "根据ID获取用户")
    @ResponseBody
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public DataResponse get(@PathVariable Long id){
        DataResponse dataResponse = super.get(id);
        Map<String, Object> data = (Map<String, Object>) dataResponse.get("data");
        User user = (User) data.get("obj");
        if(user != null){
            service.setDepotName(user);
        }
        data.put("obj", user);
        dataResponse.put("data", data);
        return dataResponse;
    }

    @Override
    @ApiOperation(value = "分页查询用户")
    @ResponseBody
    @RequestMapping(value = "page", method = RequestMethod.GET)
    public DataResponse page(@RequestParam Map<String, Object> params){
        DataResponse dataResponse = super.page(params);
        Map<String, Object> data = (Map<String, Object>) dataResponse.get("data");
        List<User> list = service.buildUserList((List<User>) data.get("list"));
        data.put("list", list);
        dataResponse.put("data", data);
        return dataResponse;
    }

    @Override
    @ApiOperation(value = "添加用户")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    @Log("添加用户")
    public DataResponse add(@CurrentUser(isFull = true) User user, @RequestBody User sysUser){
        LOGGER.info("==================开始调用 addUser ================");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        sysUser.setLastPasswordResetDate(new Date());
        sysUser.setId(SnowflakeIdWorker.generateId());
        LOGGER.info("==================结束调用 addUser ================");
        return super.add(user, sysUser);
    }


    @ApiOperation(value = "配置权限")
    @RequestMapping(value = "/setRoles",method = RequestMethod.POST)
    @ResponseBody
    @Log("配置权限")
    public DataResponse setRoles(@RequestBody Map<String, Object> params){
        return service.setRoles(params);
    }

    @ApiOperation(value = "更新用户状态")
    @Log("更新用户状态")
    @ResponseBody
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    public DataResponse updateStatus(@CurrentUser(isFull = true) User user, @RequestBody Map<String, Object> params) {
        return service.updateStatus(user, params);
    }

    @ApiOperation(value = "根据ID查询用户")
    @RequestMapping(value = "selectById", method = RequestMethod.POST)
    public User selectById(@RequestParam String userId) {
        return service.getById(userId);
    }

    @ApiOperation(value = "根据名称查询用户")
    @RequestMapping(value = "selectUserByName", method = RequestMethod.POST)
    public User selectUserByName(@RequestParam String userName) {
        return userService.selectUserByName(userName);
    }

}
