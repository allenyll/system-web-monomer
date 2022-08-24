package com.allenyll.sw.admin.controller.wx;

import com.allenyll.sw.common.constants.BaseConstants;
import com.allenyll.sw.common.entity.cms.SearchHistory;
import com.allenyll.sw.common.entity.product.Goods;
import com.allenyll.sw.common.util.*;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.system.service.cms.impl.SearchHistoryServiceImpl;
import com.allenyll.sw.system.service.file.impl.FileServiceImpl;
import com.allenyll.sw.system.service.product.impl.GoodsServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "微信商品管理")
@Controller
@RequestMapping("wx/goods")
public class WxGoodsController extends BaseController<GoodsServiceImpl, Goods> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WxGoodsController.class);

    @Autowired
    FileServiceImpl fileService;

    @Autowired
    GoodsServiceImpl goodsService;

    @Autowired
    SearchHistoryServiceImpl searchHistoryService;

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
        if (BaseConstants.STR_DEFAULT.equals(sort)) {
            // 综合排序处理
            sort = "goods_seq";
        }
        String order = MapUtil.getString(params, "order");
        boolean isAsc = true;
        if (BaseConstants.STR_ASC.endsWith(order)) {
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
