package com.allenyll.sw.admin.controller.wx;

import com.allenyll.sw.common.entity.product.Category;
import com.allenyll.sw.common.entity.product.CategoryTree;
import com.allenyll.sw.common.util.*;
import com.allenyll.sw.core.cache.util.CacheUtil;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.system.service.file.impl.FileServiceImpl;
import com.allenyll.sw.system.service.product.impl.CategoryServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags = "微信商品分类接口")
@Controller
@RequestMapping("wx/category")
public class WxCategoryController extends BaseController<CategoryServiceImpl, Category> {

    @Autowired
    FileServiceImpl fileService;

    @Autowired
    CacheUtil cacheUtil;

    @Autowired
    CategoryServiceImpl categoryService;

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
