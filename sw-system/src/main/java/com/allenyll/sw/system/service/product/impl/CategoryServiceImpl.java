package com.allenyll.sw.system.service.product.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.system.mapper.product.CategoryMapper;
import com.allenyll.sw.system.service.product.ICategoryService;
import com.allenyll.sw.common.entity.product.Category;
import org.springframework.stereotype.Service;

/**
 * 商品分类
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-03-21 10:51:04
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

}
