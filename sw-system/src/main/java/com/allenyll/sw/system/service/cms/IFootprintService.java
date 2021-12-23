package com.allenyll.sw.system.service.cms;

import com.allenyll.sw.common.entity.system.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.allenyll.sw.common.entity.cms.Footprint;
import com.allenyll.sw.common.util.Result;

import java.util.List;
import java.util.Map;

/**
 * 商品浏览记录
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2020-11-04 09:48:58
 */
public interface IFootprintService extends IService<Footprint> {

    /**
     * 获取浏览记录
     * @param customerId
     * @return
     */
    Result<List<Footprint>>  getFootprint(Long customerId);

    /**
     * 统计浏览记录总数
     * @param params
     * @return
     */
    int selectCount(Map<String, Object> params);

    /**
     * 分页查询
     * @param params
     * @return
     */
    List<Footprint> getFootprintPage(Map<String, Object> params);

    /**
     * 商品浏览记录
     * @param user
     * @param params
     */
    void saveFootprint(User user, Map<String, Object> params);

    /**
     * 删除浏览记录
     * @param user
     * @param params
     */
    void deleteFootprint(User user, Map<String, Object> params);
}
