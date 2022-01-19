package com.allenyll.sw.admin.controller.product;

import com.allenyll.sw.system.service.file.impl.FileServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.allenyll.sw.system.service.cms.impl.SearchHistoryServiceImpl;
import com.allenyll.sw.system.service.product.impl.GoodsServiceImpl;
import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.common.enums.dict.IsOrNoDict;
import com.allenyll.sw.common.dto.GoodsQueryDto;
import com.allenyll.sw.common.dto.GoodsResult;
import com.allenyll.sw.common.entity.cms.SearchHistory;
import com.allenyll.sw.common.entity.product.Goods;
import com.allenyll.sw.common.entity.product.GoodsParam;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api("商品管理相关接口")
@Controller
@RequestMapping("goods")
public class GoodsController extends BaseController<GoodsServiceImpl, Goods> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    FileServiceImpl fileService;

    @Autowired
    GoodsServiceImpl goodsService;

    @Autowired
    SearchHistoryServiceImpl searchHistoryService;

    @ApiOperation("获取ID")
    @ResponseBody
    @RequestMapping(value = "getSnowFlakeId", method = RequestMethod.POST)
    public DataResponse getSnowFlakeId() {
        Long id = SnowflakeIdWorker.generateId();
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        return DataResponse.success(result);
    }

    @ApiOperation("获取商品列表（前端展示使用）")
    @Override
    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public DataResponse list() {
        DataResponse dataResponse = super.list();
        Map<String, Object> data = (Map<String, Object>) dataResponse.get("data");
        List<Goods> list = (List<Goods>) data.get("list");
        Map<Long, String> map = new HashMap<>();
        List<Map<String, Object>> newList = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(list)){
            for(Goods goods:list){
                Map<String, Object> _map = new HashMap<>();
                map.put(goods.getId(), goods.getGoodsName());
                _map.put("label", goods.getGoodsName());
                _map.put("value", goods.getId());
                newList.add(_map);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("map", map);
        result.put("list", newList);
        return DataResponse.success(result);
    }

    @ApiOperation("获取商品列表")
    @ResponseBody
    @RequestMapping(value = "getGoodsList", method = RequestMethod.POST)
    public DataResponse getGoodsList(@RequestBody Map<String, Object> params) {
        return service.getGoodsList(params);
    }

    @ApiOperation("根据商品类型获取商品列表")
    @ResponseBody
    @RequestMapping(value = "getGoodsListByType", method = RequestMethod.POST)
    public DataResponse getGoodsListByType(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        List<Goods> list = service.getGoodsListByType(params);
        result.put("goodsList", list);
        return DataResponse.success(result);
    }

    @ApiOperation("分页查询商品")
    @Override
    @ResponseBody
    @RequestMapping(value = "page", method = RequestMethod.GET)
    public DataResponse page(@RequestParam Map<String, Object> params){
        Map<String, Object> result = new HashMap<>();

        LOGGER.info("传入参数=============" + params);
        DataResponse dataResponse = super.page(params);
        Map<String, Object> data = (Map<String, Object>) dataResponse.get("data");
        List<Goods> goodsList = (List<Goods>) data.get("list");
        if(CollectionUtil.isNotEmpty(goodsList)){
            for(Goods goods:goodsList){
                goodsService.setFile(goods);
            }
        }
        result.put("total", dataResponse.get("total"));
        result.put("list", goodsList);
        return DataResponse.success(result);
    }

    @ApiOperation("创建商品")
    @ResponseBody
    @RequestMapping(value = "/createGoods", method = RequestMethod.POST)
    public DataResponse createGoods(@CurrentUser(isFull = true) User user, @RequestBody GoodsParam goodsParam) {
        LOGGER.debug("保存参数：{}", goodsParam);
        Map<String, Object> result = new HashMap<>();
        try {
            int count = goodsService.createGoods(goodsParam, user);
            result.put("count", count);
        } catch (Exception e) {
            LOGGER.error("创建商品失败");
            e.printStackTrace();
        }

        return DataResponse.success(result);
    }

    @ApiOperation("更新商品")
    @ResponseBody
    @RequestMapping(value = "/updateGoods/{id}", method = RequestMethod.POST)
    public DataResponse updateGoods(@CurrentUser(isFull = true) User user, @PathVariable String id, @RequestBody GoodsParam goodsParam) {
        LOGGER.debug("更新参数：{}", goodsParam);
        Map<String, Object> result = new HashMap<>();
        try {
            int count = goodsService.updateGoods(goodsParam, user);
            result.put("count", count);
        } catch (Exception e) {
            LOGGER.error("创建商品失败");
            e.printStackTrace();
        }

        return DataResponse.success(result);
    }

    @ApiOperation("删除商品")
    @ResponseBody
    @RequestMapping(value = "/deleteGoods/{id}", method = RequestMethod.POST)
    public DataResponse deleteGoods(@CurrentUser(isFull = true) User user, @PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            int count = goodsService.deleteGoods(user, id);
            result.put("count", count);
        } catch (Exception e) {
            LOGGER.error("创建商品失败");
            e.printStackTrace();
        }

        return DataResponse.success(result);
    }

    /**
     * 更新商品状态
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateLabel", method = RequestMethod.POST)
    public DataResponse updateLabel(@CurrentUser(isFull = true) User user, @RequestBody Map<String, Object> params) {
        return service.updateLabel(user, params);
    }

    @Override
    @ApiOperation("根据ID获取商品")
    @ResponseBody
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public DataResponse get(@PathVariable Long id) {

        Map<String, Object> result = null;

        DataResponse dataResponse = super.get(id);
        Map<String, Object> data = (Map<String, Object>) dataResponse.get("data");
        Goods goods = (Goods) data.get("obj");

        if(goods == null) {
            return  DataResponse.fail("获取商品失败");
        }
        goodsService.setFile(goods);
        try {
            result = goodsService.getGoodsInfo(goods);
        } catch (Exception e) {
            LOGGER.error("赋值异常");
            e.printStackTrace();
        }

        return DataResponse.success(result);
    }

    @ApiOperation("获取商品列表")
    @ResponseBody
    @RequestMapping(value = "/getGoodsListByCondition", method = RequestMethod.POST)
    public Result<GoodsResult> getGoodsListByCondition(@RequestBody GoodsQueryDto goodsQueryDto){
        return service.getGoodsListByCondition(goodsQueryDto);
    }

    @ApiOperation("获取商品库存信息")
    @ResponseBody
    @RequestMapping(value = "/getStock", method = RequestMethod.POST)
    public Result<GoodsResult> getStock(@RequestBody GoodsQueryDto goodsQueryDto){
        return service.getStock(goodsQueryDto);
    }

    @ApiOperation("导入商品")
    @ResponseBody
    @RequestMapping(value = "/importGoods", method = RequestMethod.POST)
    public Result<GoodsResult> importGoods(@RequestBody GoodsQueryDto goodsQueryDto, @CurrentUser(isFull = true) User user){
        return service.importGoods(goodsQueryDto, user);
    }

    @ApiOperation("[小程序接口]小程序获取商品详情")
    @ResponseBody
    @RequestMapping(value = "/getGoodsInfo/{id}", method = RequestMethod.POST)
    public Result getGoodsInfo(@PathVariable Long id){
        Result result = new Result();
        Map<String, Object> dataMap = new HashMap<>();
        DataResponse dataResponse = super.get(id);
        Map<String, Object> data = (Map<String, Object>) dataResponse.get("data");
        Goods goods = (Goods) data.get("obj");
        if(goods == null){
            result.fail("商品不存在");
            return result;
        }
        goodsService.setFile(goods);

        try {
            dataMap = goodsService.getGoodsInfo(goods);
        } catch (Exception e) {
            LOGGER.error("赋值异常：", e.getMessage());
        }
        result.setData(dataMap);
        return result;
    }

    @ApiOperation("[小程序接口]根据分类获取商品")
    @ResponseBody
    @RequestMapping(value = "/getGoods", method = RequestMethod.POST)
    public Result getGoods(@RequestBody Map<String, Object> params){
        Result result = new Result();
        Map<String, Object> dataMap = service.getGoodsByCategory(params);
        result.setData(dataMap);
        return result;
    }

    @ApiOperation("[小程序接口]查询商品")
    @ResponseBody
    @RequestMapping(value = "/searchGoods", method = RequestMethod.POST)
    public Result searchGoods(@RequestBody Map<String, Object> params){
        Result result = new Result();
        Map<String, Object> data = new HashMap<>();

        page = MapUtil.getIntValue(params, "page");
        limit = MapUtil.getIntValue(params, "limit");
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("IS_DELETE", 0);
        String sort = MapUtil.getString(params, "sort");
        if ("default".equals(sort)) {
            // 综合排序处理
            sort = "goods_seq";
        }
        String order = MapUtil.getString(params, "order");
        boolean isAsc = true;
        if ("asc".endsWith(order)) {
            isAsc = true;
        } else {
            isAsc = false;
        }
        wrapper.orderBy(true, isAsc, sort);
        String keyword = MapUtil.getString(params, "keyword");
        if (StringUtil.isNotEmpty(keyword)) {
            String time = DateUtil.getCurrentDateTime();
            // 新增搜索记录
            Long customerId = MapUtil.getLong(params, "userId");
            if (StringUtil.isEmpty(customerId)) {
                LOGGER.error("关联用户为空，无法查询");
                result.fail("关联用户为空，无法查询");
                return result;
            }
            SearchHistory searchHistory = new SearchHistory();
            searchHistory.setId(SnowflakeIdWorker.generateId());
            searchHistory.setDataSource("小程序");
            searchHistory.setKeyword(keyword);
            searchHistory.setUserId(customerId);
            searchHistory.setIsDelete(0);
            searchHistory.setAddTime(time);
            searchHistory.setAddUser(customerId);
            searchHistory.setUpdateUser(customerId);
            searchHistory.setUpdateTime(time);
            searchHistoryService.save(searchHistory);
        }
        wrapper.like("KEYWORDS", keyword);
        String categoryId = MapUtil.getMapValue(params, "categoryId");
        if (StringUtil.isNotEmpty(categoryId)) {
            wrapper.eq("CATEGORY_ID", categoryId);
        }

        int total = goodsService.count(wrapper);
        Page<Goods> pages = service.page(new Page<>(page, limit), wrapper);
        List<Goods> list = pages.getRecords(); 
        if(CollectionUtil.isNotEmpty(list)){
            for (Goods goods: list){
                goodsService.setFile(goods);
            }
        }

        int num = total%limit;
        if(num == 0){
            totalPage = total/limit;
        }else{
            totalPage = total/limit + 1;
        }

        data.put("currentPage", page);
        data.put("totalPage", totalPage);
        data.put("goods", list);    
        result.setData(data);
        return result;
    }

}
