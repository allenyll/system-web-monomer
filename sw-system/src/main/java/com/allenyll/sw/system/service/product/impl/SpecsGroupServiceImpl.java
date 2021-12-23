package com.allenyll.sw.system.service.product.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.system.mapper.product.SpecsGroupMapper;
import com.allenyll.sw.system.service.product.ISpecsGroupService;
import com.allenyll.sw.common.entity.product.SpecsGroup;
import org.springframework.stereotype.Service;

/**
 * 规格组
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-05-13 16:05:43
 */
@Service("specsGroupService")
public class SpecsGroupServiceImpl extends ServiceImpl<SpecsGroupMapper, SpecsGroup> implements ISpecsGroupService {

}
