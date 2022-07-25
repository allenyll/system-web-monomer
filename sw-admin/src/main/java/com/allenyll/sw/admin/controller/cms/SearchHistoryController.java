package com.allenyll.sw.admin.controller.cms;

import com.allenyll.sw.common.entity.cms.SearchHistory;
import com.allenyll.sw.common.util.*;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.system.service.cms.impl.SearchHistoryServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:  
 * @Author:       allenyll
 * @Date:         2020/8/12 10:05 下午
 * @Version:      1.0
 */
@Slf4j
@Api(value = "搜索记录接口", tags = "搜索记录管理")
@RestController
@RequestMapping("searchHistory")
public class SearchHistoryController extends BaseController<SearchHistoryServiceImpl, SearchHistory> {

    @Autowired
    SearchHistoryServiceImpl searchHistoryService;

    @ApiOperation("[小程序接口]清除搜索记录")
    @ResponseBody
    @RequestMapping(value = "clearHistoryKeyword", method = RequestMethod.POST)
    public Result clearHistoryKeyword(@RequestBody Map<String, Object> params){
        Result result = new Result();
        String customerId = MapUtil.getString(params, "userId");
        if (StringUtil.isEmpty(customerId)) {
            log.error("关联用户为空，无法查询");
            result.fail("关联用户为空，无法查询");
            return result;
        }
        params.put("time", DateUtil.getCurrentDateTime());

        int num = searchHistoryService.updateByCustomerId(params);

        return result;
    }

    @ApiOperation("新增搜索记录")
    @RequestMapping(value = "insert", method = RequestMethod.POST)
    public void insert(@RequestBody SearchHistory searchHistory) {
        service.save(searchHistory);
    }

}
