package com.allenyll.sw.system.service.product.impl;

import com.allenyll.sw.common.constants.BaseConstants;
import com.allenyll.sw.common.entity.product.CategoryTree;
import com.allenyll.sw.common.entity.system.File;
import com.allenyll.sw.common.enums.dict.FileDict;
import com.allenyll.sw.common.enums.dict.StatusDict;
import com.allenyll.sw.common.exception.BusinessException;
import com.allenyll.sw.common.util.CollectionUtil;
import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.common.util.StringUtil;
import com.allenyll.sw.common.util.TreeUtil;
import com.allenyll.sw.system.service.file.impl.FileServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.system.mapper.product.CategoryMapper;
import com.allenyll.sw.system.service.product.ICategoryService;
import com.allenyll.sw.common.entity.product.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品分类
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-03-21 10:51:04
 */
@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    protected static final String DEFAULT_URL = "https://system-web-1257935390.cos.ap-chengdu.myqcloud.com/images/no.jpeg";

    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private FileServiceImpl fileService;
    
    @Override
    public List<CategoryTree> tree(String name) {
        log.info("开始获取商品类目树形结构：{}", name);
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("IS_DELETE", 0);
        wrapper.orderBy(true, true, "CATEGORY_SEQ");
        if(StringUtil.isNotEmpty(name)){
            wrapper.like("CATEGORY_NAME", name);
        }
        
        List<Category> categories = categoryMapper.selectList(wrapper);

        if(CollectionUtil.isNotEmpty(categories)){
            for(Category category:categories){
                QueryWrapper<File> fileEntityWrapper = new QueryWrapper<>();
                fileEntityWrapper.eq("FILE_TYPE", FileDict.CATEGORY.getCode());
                fileEntityWrapper.eq("IS_DELETE", 0);
                fileEntityWrapper.eq("FK_ID", category.getId());
                List<File> sysFiles = fileService.list(fileEntityWrapper);
                if(CollectionUtil.isNotEmpty(sysFiles)){
                    category.setFileUrl(sysFiles.get(0).getFileUrl());
                }else{
                    category.setFileUrl(DEFAULT_URL);
                }
            }
        }

        List<CategoryTree> categoryTrees = getCategoryTree(categories, BaseConstants.MENU_ROOT);
        return categoryTrees;
    }

    @Override
    public List<CategoryTree> categoryTree() {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("IS_DELETE", 0);
        wrapper.eq("IS_USED", StatusDict.START.getCode());
        wrapper.orderBy(true, true, "CATEGORY_SEQ");
        List<Category> list = categoryMapper.selectList(wrapper);
        if(!CollectionUtils.isEmpty(list)){
            for(Category _category:list){
                setParentCategory(_category);
            }
        }

        Category category = new Category();
        category.setId(0L);
        category.setIsDelete(0);
        category.setCategoryName("顶级节点");
        category.setCategoryNo("top");
        category.setParentId(1000000L);
        list.add(category);

        List<CategoryTree> trees = getCategoryTree(list, 1000000L);
        return trees;
    }

    @Override
    public Map<String, Object> getCategoryInfo(Long id) {
        Map<String, Object> result = new HashMap<>();
        Category category = categoryMapper.selectById(id);
        // 获取同级分类
        if(category == null){
            throw new BusinessException("没有获取到对应的分类");
        }

        // 获取子分类
        QueryWrapper<Category> childWrapper = new QueryWrapper<>();
        childWrapper.eq("PARENT_ID", category.getParentId());
        childWrapper.eq("IS_USED", StatusDict.START.getCode());
        childWrapper.eq("IS_DELETE", 0);
        childWrapper.orderBy(true, false, "CATEGORY_NO");

        List<Category> brotherCategoryList = categoryMapper.selectList(childWrapper);

        List<CategoryTree> trees = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(brotherCategoryList)){
            for (Category _category:brotherCategoryList){
                CategoryTree categoryTree = setCategoryTree(_category);
                trees.add(categoryTree);
            }
        }

        result.put("list", trees);
        result.put("obj", setCategoryTree(category));
        return result;
    }

    @Override
    public Map<String, Object> getCategoryById(Long id) {
        Map<String, Object> result = new HashMap<>();
        Category category = categoryMapper.selectById(id);
        // 获取同级分类
        if(category == null){
            throw new BusinessException("没有获取到对应的分类");
        }

        if(BaseConstants.MENU_ROOT.equals(id)){
            category = new Category();
            category.setId(id);
            category.setCategoryName("顶级节点");
        }else{
            setParentCategory(category);
        }

        File file = getFile(category);
        if(file != null){
            category.setFileUrl(file.getFileUrl());
        }else{
            file = new File();
        }

        // 获取子分类
        QueryWrapper<Category> childWrapper = new QueryWrapper<>();
        childWrapper.eq("PARENT_ID", id);
        childWrapper.eq("IS_DELETE", 0);
        childWrapper.eq("IS_USED", StatusDict.START.getCode());
        childWrapper.orderBy(true, true, "CATEGORY_SEQ");
        List<Category> childCategoryList = categoryMapper.selectList(childWrapper);
        childCategoryList.add(category);
        for(Category child:childCategoryList){
            File _file = getFile(child);
            if(_file != null){
                child.setFileUrl(_file.getFileUrl());
            } else {
                child.setFileUrl(DEFAULT_URL);
            }
        }

        if(!(0L == id)){
            List<CategoryTree> trees = getCategoryTree(childCategoryList, 0L);
            result.put("tree", trees);
        }
        result.put("file", file);
        result.put("obj", category);
        return result;
    }

    /**
     * 封装分类树形结构
     * @param list
     * @param rootId
     * @return
     */
    private List<CategoryTree> getCategoryTree(List<Category> list, Long rootId) {
        List<CategoryTree> trees = new ArrayList<>();
        CategoryTree tree;
        if(CollectionUtil.isEmpty(list)){
            return trees;
        }
        for(Category obj:list){
            tree = new CategoryTree();
            tree.setId(obj.getId());
            tree.setParentId(obj.getParentId());
            tree.setName(obj.getCategoryName());
            tree.setCode(obj.getCategoryNo());
            tree.setTitle(obj.getCategoryName());
            tree.setLabel(obj.getCategoryName());
            tree.setLevel(obj.getCategoryLevel());
            tree.setIsUsed(StatusDict.codeToMessage(obj.getIsUsed()));
            tree.setUrl(obj.getFileUrl());
            trees.add(tree);
        }
        return TreeUtil.build(trees, rootId);
    }

    /**
     * 设置父节点
     * @param category
     */
    private void setParentCategory(Category category) {
        Long parentId = category.getParentId();
        if(parentId.equals(BaseConstants.MENU_ROOT)){
            category.setParentCategoryName("顶级节点");
        }else{
            QueryWrapper<Category> entityWrapper = new QueryWrapper<>();
            entityWrapper.eq("IS_DELETE", 0);
            entityWrapper.eq("ID", parentId);
            Category _category = categoryMapper.selectOne(entityWrapper);
            if(_category != null){
                category.setParentCategoryName(_category.getCategoryName());
            }
        }
    }

    /**
     * 填充数据
     * @param category
     * @return
     */
    private CategoryTree setCategoryTree(Category category){
        CategoryTree categoryTree = new CategoryTree();
        categoryTree.setId(category.getId());
        categoryTree.setParentId(category.getParentId());
        categoryTree.setName(category.getCategoryName());
        categoryTree.setCode(category.getCategoryNo());
        categoryTree.setLabel(category.getCategoryName());
        categoryTree.setTitle(category.getCategoryName());
        categoryTree.setLevel(category.getCategoryLevel());
        categoryTree.setIsUsed(StatusDict.codeToMessage(category.getIsUsed()));
        return categoryTree;
    }

    /**
     * 分类图片
     * @param category
     * @return
     */
    private File getFile(Category category) {
        QueryWrapper<File> fileEntityWrapper = new QueryWrapper<>();
        fileEntityWrapper.eq("FILE_TYPE", FileDict.CATEGORY.getCode());
        fileEntityWrapper.eq("IS_DELETE", 0);
        fileEntityWrapper.eq("FK_ID", category.getId());
        List<File> _sysFiles = fileService.list(fileEntityWrapper);
        File _file = null;
        if(CollectionUtil.isNotEmpty(_sysFiles)){
            _file = _sysFiles.get(0);
        }
        return _file;
    }
}
