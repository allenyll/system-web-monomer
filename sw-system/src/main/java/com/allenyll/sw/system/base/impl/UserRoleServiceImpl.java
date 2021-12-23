package com.allenyll.sw.system.base.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.common.entity.system.SysUserRole;
import com.allenyll.sw.system.mapper.sys.UserRoleMapper;
import com.allenyll.sw.system.base.IUserRoleService;
import org.springframework.stereotype.Service;

/**
 * @Description:  用户<user>服务实现
 * @Author:       allenyll
 * @Date:         2020/5/4 8:50 下午
 * @Version:      1.0
 */
@Service("userRoleService")
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, SysUserRole> implements IUserRoleService {
}
