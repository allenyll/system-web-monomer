package com.allenyll.sw.system.base.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.common.entity.system.SysRoleMenu;
import com.allenyll.sw.system.mapper.sys.SysRoleMenuMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 权限菜单关系 服务实现类
 * </p>
 *
 * @author yu.leilei
 * @since 2018-11-13
 */
@Service("roleMenuService")
public class RoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> {

}
