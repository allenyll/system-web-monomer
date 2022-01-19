package com.allenyll.sw.admin.controller.product;

import com.allenyll.sw.core.cache.util.CacheUtil;
import com.allenyll.sw.system.service.file.impl.FileServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.allenyll.sw.system.service.product.impl.CategoryServiceImpl;
import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.common.constants.BaseConstants;
import com.allenyll.sw.common.enums.dict.FileDict;
import com.allenyll.sw.common.enums.dict.StatusDict;
import com.allenyll.sw.common.entity.product.Category;
import com.allenyll.sw.common.entity.product.CategoryTree;
import com.allenyll.sw.common.entity.system.File;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags = "商品分类接口")
@Controller
@RequestMapping("category")
public class CategoryController extends BaseController<CategoryServiceImpl, Category> {


    @Autowired
    FileServiceImpl fileService;

    @Autowired
    CacheUtil cacheUtil;

    @Autowired
    CategoryServiceImpl categoryService;

    @Override
    @ApiOperation("组装分类前台展示")
    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public DataResponse list() {
        DataResponse dataResponse = super.list();
        Map<String, Object> data = (Map<String, Object>) dataResponse.get("data");
        List<Category> list = (List<Category>) data.get("list");
        Map<Long, String> map = new HashMap<>();
        List<Map<String, Object>> newList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(list)){
            for(Category category:list){
              /*  if("0".equals(category.getParentId())){
                    continue;
                }*/
                Map<String, Object> _map = new HashMap<>();
                map.put(category.getId(), category.getCategoryName());
                _map.put("label", category.getCategoryName());
                _map.put("value", category.getId());
                _map.put("parentId", category.getParentId());
                newList.add(_map);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("map", map);
        result.put("list", newList);
        return DataResponse.success(result);
    }

    @ApiOperation("获取分类树")
    @ResponseBody
    @RequestMapping(value = "tree", method = RequestMethod.GET)
    public DataResponse tree(String name){
        log.info("============= {开始调用方法：tree(} =============");
        Map<String, Object> result = new HashMap<>();
        List<CategoryTree> categoryTrees = service.tree(name);
        result.put("list", categoryTrees);
        log.info("============= {结束调用方法：tree(} =============");
        return DataResponse.success(result);
    }

    @ApiOperation("获取分类树")
    @ResponseBody
    @RequestMapping(value = "categoryTree", method = RequestMethod.GET)
    public DataResponse categoryTree(){
        List<CategoryTree> trees = service.categoryTree();
        Map<String, Object> result = new HashMap<>();
        result.put("categoryTree", trees);
        return DataResponse.success(result);
    }

    @ApiOperation("[小程序接口]获取分类详情")
    @ResponseBody
    @RequestMapping(value = "getCategoryInfo/{id}", method = RequestMethod.GET)
    public Result getCategoryInfo(@PathVariable Long id){
        Result result = new Result();
        Map<String, Object> data;
        try {
            data = service.getCategoryInfo(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            result.fail(e.getMessage());
            return result;
        }
        result.setData(data);
        log.info("==================结束调用 get================");
        return result;
    }

    @Override
    @ApiOperation("根据ID获取分类")
    @ResponseBody
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public DataResponse get(@PathVariable Long id){
        Map<String, Object> data;
        try {
            data = service.getCategoryById(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            return DataResponse.fail(e.getMessage());
        }
        log.info("==================结束调用 get================");
        return DataResponse.success(data);
    }

    @ApiOperation("添加分类")
    @Override
    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public DataResponse add(@CurrentUser(isFull = true) User user, @RequestBody Category category) {
        Long id = SnowflakeIdWorker.generateId();
        category.setId(id);
        super.add(user, category);
        List<Map<String, String>> fileList = category.getFileList();
        if(CollectionUtil.isNotEmpty(fileList)){
            Map<String, String> map = fileList.get(0);
            String url = MapUtil.getMapValue(map, "url");
            Long userId = user.getId();
            // 存入数据库
            File sysFile = new File();
            sysFile.setId(SnowflakeIdWorker.generateId());
            sysFile.setFileType(FileDict.CATEGORY.getCode());
            sysFile.setFkId(id);
            sysFile.setFileUrl(url);
            sysFile.setAddTime(DateUtil.getCurrentDateTime());
            sysFile.setIsDelete(0);
            sysFile.setAddUser(userId);
            sysFile.setUpdateTime(DateUtil.getCurrentDateTime());
            sysFile.setUpdateUser(userId);
            fileService.save(sysFile);
        }
        return DataResponse.success();
    }

    @ApiOperation("更新分类")
    @Override
    @ResponseBody
    @RequestMapping(value = "{id}",method = RequestMethod.PUT)
    public DataResponse update(@CurrentUser(isFull = true) User user, @RequestBody Category category) {
        List<Map<String, String>> fileList = category.getFileList();
        if(CollectionUtil.isEmpty(fileList)){
            // 删除对应的图片
            fileService.deleteFile(category.getId());
        }
        return super.update(user, category);
    }

    @ApiOperation("[小程序接口]获取分类树")
    @ResponseBody
    @RequestMapping(value = "getCategoryTree", method = RequestMethod.GET)
    public Result getCategoryTree(String name){
        log.info("============= {开始调用方法：getCategoryTree(} =============");
        Result result = new Result();
        Map<String, Object> data = new HashMap<>();
        List<CategoryTree> categoryTrees = service.tree(name);
        data.put("list", categoryTrees);
        result.setData(data);
        log.info("============= {结束调用方法：getCategoryTree(} =============");
        return result;
    }

    @ApiOperation("[小程序接口]根据ID获取分类")
    @ResponseBody
    @RequestMapping(value = "/getCategory/{id}", method = RequestMethod.GET)
    public Result getCategory(@PathVariable Long id){
        Result result = new Result();
        Map<String, Object> data;
        try {
            data = service.getCategoryById(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            result.fail(e.getMessage());
            return result;
        }
        result.setData(data);
        return result;
    }
}
