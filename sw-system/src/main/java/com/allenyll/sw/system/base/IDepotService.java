package com.allenyll.sw.system.base;

import com.allenyll.sw.common.util.DataResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.allenyll.sw.common.entity.system.Depot;

/**
 * @Description:  部门<Depot>服务接口
 * @Author:       allenyll
 * @Date:         2020/5/25 7:30 下午
 * @Version:      1.0
 */
public interface IDepotService extends IService<Depot> {

    /**
     * 获取部门列表
     * @param name
     * @return
     */
    DataResponse getDepotList(String name);
    
    /**
     * 获取所有部门--树形结构
     * @return
     */
    DataResponse getDepotTree();

    /**
     * 根据部门ID获取具体部门
     * @param id
     * @return
     */
    DataResponse getDepot(Long id);

}
