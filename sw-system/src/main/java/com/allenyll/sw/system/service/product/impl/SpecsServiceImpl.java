package com.allenyll.sw.system.service.product.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.system.mapper.product.SpecsMapper;
import com.allenyll.sw.system.service.product.ISpecsService;
import com.allenyll.sw.common.entity.product.Specs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 规格表
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-05-13 16:08:41
 */
@Service("specsService")
public class SpecsServiceImpl extends ServiceImpl<SpecsMapper, Specs> implements com.allenyll.sw.system.service.product.ISpecsService {

    @Autowired
    SpecsMapper specsMapper;

    @Override
    public Map<String, Object> getSpecs(String id) {
        return specsMapper.getSpecs(id);
    }

}
