package com.allenyll.sw.system.base;

import com.allenyll.sw.common.util.DataResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.allenyll.sw.common.entity.system.Menu;

import java.util.Map;

/**
 * @Description:  菜单<Menu>服务接口
 * @Author:       allenyll
 * @Date:         2020/5/4 8:47 下午
 * @Version:      1.0
 */
public interface IMenuService extends IService<Menu> {

    /**
     * 根据菜单ID获取菜单
     * @param id
     * @return
     */
    DataResponse getMenuById(Long id);

    /**
     * 组装菜单树
     * @param type
     * @return
     */
    DataResponse getMenuTreeList(String type);
}
