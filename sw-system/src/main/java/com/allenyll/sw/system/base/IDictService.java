package com.allenyll.sw.system.base;

import com.allenyll.sw.common.util.DataResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.allenyll.sw.common.entity.system.Dict;

/**
 * @Description:  字典<Dict>服务接口
 * @Author:       allenyll
 * @Date:         2020/5/25 7:30 下午
 * @Version:      1.0
 */
public interface IDictService extends IService<Dict> {

    /**
     * 根据字典code获取具体描述
     * @param code
     * @return
     */
    Dict getDictByCode(String code);

    /**
     * 字典列表
     * @param code
     * @return
     */
    DataResponse getDictList(String code);

    /**
     * 获取 parentId = 0 的字典集合
     * @return
     */
    DataResponse getParent();

    /**
     * 获取子字典集合
     * @param id
     * @return
     */
    DataResponse getChild(String id);
}
