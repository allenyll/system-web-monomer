package com.allenyll.sw.system.base.impl;

import com.allenyll.sw.common.entity.system.Depot;
import com.allenyll.sw.common.enums.dict.UserStatus;
import com.allenyll.sw.common.util.*;
import com.allenyll.sw.system.mapper.sys.DepotMapper;
import com.allenyll.sw.system.mapper.sys.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.common.entity.system.SysUserRole;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.system.base.IUserService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:  用户<user>服务实现
 * @Author:       allenyll
 * @Date:         2020/5/4 8:50 下午
 * @Version:      1.0
 */
@Slf4j
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    UserMapper userMapper;
    
    @Resource
    private DepotMapper depotMapper;

    @Autowired
    UserRoleServiceImpl userRoleService;

    @Override
    public List<Map<String, Object>> getUserRoleMenuList(Map<String, Object> params) {
        return userMapper.getUserRoleMenuList(params);
    }

    @Override
    public User selectUserByName(String userName) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_name", userName);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public Result getUserInfo(String token) {
        Result result = new Result();
        Map<String, Object> resultMap = new HashMap<>(5);
        try {
            System.out.println(token);
            Claims claims = JwtUtil.verifyToken(token);
            String username = (String) claims.get("username");
            String clientId = (String) claims.get("client_id");
            String jti = (String) claims.get("jti");
            // 获取用户
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("ID", username);
            wrapper.eq("STATUS", UserStatus.OK.getCode());
            wrapper.eq("IS_DELETE", 0);
            User user = userMapper.selectOne(wrapper);
            if(user != null){
                Long userId = user.getId();
                Map<String, Object> param = new HashMap<>(1);
                param.put("user_id", userId);
                // 根据userId查询用户角色
                List<Map<String, Object>> menuList = userMapper.getUserRoleMenuList(param);
                QueryWrapper<SysUserRole> roleQueryWrapper = new QueryWrapper<>();
                roleQueryWrapper.eq("user_id", userId);
                SysUserRole sysUserRole = userRoleService.getOne(roleQueryWrapper);
                resultMap.put("user", user);
                resultMap.put("name", user.getUserName());
                resultMap.put("menus", menuList);
                resultMap.put("roles", sysUserRole);
                result.setData(resultMap);
            }else{
                log.info("没有查询到用户!");
                result.fail("查询用户失败");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.fail("认证用户失败！");
            return result;
        }
        return result;
    }

    @Override
    public DataResponse updateStatus(User user, Map<String, Object> params) {
        log.info("保存参数：{}", params);
        Map<String, Object> result = new HashMap<>();
        Long id = MapUtil.getLong(params, "id");
        String status = MapUtil.getString(params, "status");
        User sysUser = userMapper.selectById(id);
        if(sysUser == null){
            return DataResponse.fail("更新失败, 用户不存在");
        }
        sysUser.setStatus(status);
        sysUser.setUpdateTime(DateUtil.getCurrentDateTime());
        sysUser.setUpdateUser(user.getId());
        try {
            userMapper.updateById(sysUser);
        } catch (Exception e) {
            return DataResponse.fail("更新用户状态失败");
        }

        return DataResponse.success(result);
    }

    @Override
    public DataResponse setRoles(Map<String, Object> params) {
        log.info("==================开始调用 setRoles ================");
        log.info("params"+params);
        // 全删全插配置用户角色
        // 1、先删除所有该用户拥有的角色
        Long userId = Long.parseLong(params.get("id").toString());
        QueryWrapper<SysUserRole> userRoleEntityWrapper = new QueryWrapper<>();
        userRoleEntityWrapper.eq("user_id", userId);
        userRoleService.remove(userRoleEntityWrapper);

        // 2、重新插入选择的角色权限
        List<SysUserRole> list = new ArrayList<>();
        JSONArray jsonArray = JSONArray.fromObject(params.get("ids"));
        if(jsonArray.size() > 0){
            for(int i=0; i<jsonArray.size(); i++){
                Long roleId = Long.parseLong(jsonArray.getString(i));
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setRoleId(roleId);
                sysUserRole.setUserId(userId);
                sysUserRole.setId(SnowflakeIdWorker.generateId());
                list.add(sysUserRole);
            }
        }
        userRoleService.saveBatch(list);
        log.info("==================结束调用 setRoles ================");
        return DataResponse.success();
    }

    public List<User>  buildUserList(List<User> list) {
        if(!CollectionUtils.isEmpty(list)){
            for(User user:list){
                setDepotName(user);
            }
        }
        return list;
    }

    public void setDepotName(User user){
        Long depotId = user.getDepotId();
        QueryWrapper<Depot> depotEntityWrapper = new QueryWrapper<>();
        depotEntityWrapper.eq("is_delete", 0);
        depotEntityWrapper.eq("id", depotId);
        Depot depot = depotMapper.selectOne(depotEntityWrapper);
        if(depot != null){
            user.setDepotName(depot.getDepotName());
        }
    }
}
