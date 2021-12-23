package com.allenyll.sw.system.base;

import com.allenyll.sw.common.util.DataResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.Result;

import java.util.List;
import java.util.Map;

/**
 * @Description:  用户<User>服务接口
 * @Author:       allenyll
 * @Date:         2020/5/4 8:47 下午
 * @Version:      1.0
 */
public interface IUserService extends IService<User> {

    /**
     * 根据用户信息获取菜单
     * @param params
     * @return
     */
    List<Map<String, Object>> getUserRoleMenuList(Map<String, Object> params);

    /**
     * 根据用户名查询用户
     * @param userName
     * @return
     */
    User selectUserByName(String userName);

    /**
     * 根据token获取用户信息
     * @param token
     * @return
     */
    Result getUserInfo(String token);

    /**
     * 更新用户状态
     * @param user
     * @param params
     * @return
     */
    DataResponse updateStatus(User user, Map<String, Object> params);

    /**
     * 配置角色
     * @param params
     * @return
     */
    DataResponse setRoles(Map<String, Object> params);
}
