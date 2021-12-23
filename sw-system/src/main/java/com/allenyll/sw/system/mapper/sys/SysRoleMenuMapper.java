package com.allenyll.sw.system.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.allenyll.sw.common.entity.system.SysRoleMenu;
import org.springframework.stereotype.Repository;

/**
 * <p>
  * 权限菜单关系 Mapper 接口
 * </p>
 *
 * @author yu.leilei
 * @since 2018-11-13
 */
@Repository("sysRoleMenuMapper")
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

}
