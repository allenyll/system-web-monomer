package com.allenyll.sw.system.base.impl;

import com.allenyll.sw.common.entity.system.SysRoleMenu;
import com.allenyll.sw.common.entity.system.SysUserRole;
import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.common.util.SnowflakeIdWorker;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.common.entity.system.Role;
import com.allenyll.sw.system.mapper.sys.RoleMapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("roleService")
@Transactional(rollbackFor = Exception.class)
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> {

    @Autowired
    private UserRoleServiceImpl userRoleService;

    @Autowired
    private RoleMenuServiceImpl roleMenuService;
    
    @Autowired
    private RoleMapper roleMapper;

    /**
     * 获取角色列表
     * @param id
     * @return
     */
    public DataResponse getAllRole(String id) {
        log.info("============= {开始调用方法：getAllRole(} =============");
        Map<String, Object> result = new HashMap<>();
        // 获取用户拥有的角色
        QueryWrapper<SysUserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", id);
        SysUserRole user = userRoleService.getOne(wrapper);
        Role role;
        if (user != null) {
            Long roleId = user.getRoleId();
            QueryWrapper<Role> roleEntityWrapper = new QueryWrapper<>();
            roleEntityWrapper.eq("is_delete", 0);
            roleEntityWrapper.eq("id", roleId);
            role = roleMapper.selectOne(roleEntityWrapper);
            result.put("userRole", role);
        } else {
            result.put("userRole", "");
        }
        QueryWrapper<Role> userWrapper = new QueryWrapper<>();
        userWrapper.eq("is_delete", 0);
        List<Role> roles = roleMapper.selectList(userWrapper);
        result.put("roleList", roles);
        log.info("============= {结束调用方法：getAllRole(} =============");
        return DataResponse.success(result);
    }

    /**
     * 配置角色菜单
     * @param params
     * @return
     */
    public DataResponse setMenus(Map<String, Object> params) {
        log.info("==================开始调用 setMenus ================");
        log.info("params" + params);
        // 全删全插配置角色菜单
        // 1、先删除所有该角色拥有的菜单权限
        Long roleId = Long.parseLong((String) params.get("id"));
        QueryWrapper<SysRoleMenu> roleMenuEntityWrapper = new QueryWrapper<>();
        roleMenuEntityWrapper.eq("role_id", roleId);
        roleMenuService.remove(roleMenuEntityWrapper);

        // 2、重新插入选择的菜单权限
        List<SysRoleMenu> list = new ArrayList<>();
        JSONArray jsonArray = JSONArray.fromObject(params.get("ids"));
        if(jsonArray.size() > 0){
            for(int i=0; i<jsonArray.size(); i++){
                Long menuId = Long.parseLong(jsonArray.getString(i));
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setRoleId(roleId);
                sysRoleMenu.setMenuId(menuId);
                sysRoleMenu.setId(SnowflakeIdWorker.generateId());
                list.add(sysRoleMenu);
            }
        }
        roleMenuService.saveBatch(list);

        log.info("==================结束调用 setMenus ================");
        return DataResponse.success();
    }

    /**
     * 获取角色授权菜单
     * @param id
     * @return
     */
    public DataResponse getRoleMenus(String id) {
        log.info("获取角色已选择菜单 id = " + id);
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<SysRoleMenu> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id", id);
        List<SysRoleMenu> sysRoleMenus = roleMenuService.list(wrapper);
        List<Long> list = new ArrayList<>();
        if(!CollectionUtils.isEmpty(sysRoleMenus)){
            for(SysRoleMenu roleMenu:sysRoleMenus){
                list.add(roleMenu.getMenuId());
            }
        }
        result.put("list", list);
        return DataResponse.success(result);
    }
}
