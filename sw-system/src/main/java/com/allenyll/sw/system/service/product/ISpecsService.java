package com.allenyll.sw.system.service.product;

import com.baomidou.mybatisplus.extension.service.IService;
import com.allenyll.sw.common.entity.product.Specs;

import java.util.Map;

public interface ISpecsService extends IService<Specs> {

    /**
     * 根据ID获取规格
     * @param id
     * @return
     */
    Map<String, Object> getSpecs(String id);

}
