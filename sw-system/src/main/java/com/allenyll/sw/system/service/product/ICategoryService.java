package com.allenyll.sw.system.service.product;

import com.allenyll.sw.common.entity.product.CategoryTree;
import com.baomidou.mybatisplus.extension.service.IService;
import com.allenyll.sw.common.entity.product.Category;

import java.util.List;
import java.util.Map;

public interface ICategoryService extends IService<Category> {

    /**
     * 分类树
     * @param name
     * @return
     */
    List<CategoryTree> tree(String name);

    /**
     * 初始化商品分类
     * @return
     */
    List<CategoryTree> categoryTree();

    /**
     * 根据ID获取分类信息
     * @param id
     * @return
     */
    Map<String, Object> getCategoryInfo(Long id);

    /**
     * 获取分类信息
     * @param id
     * @return
     */
    Map<String, Object> getCategoryById(Long id);
}
