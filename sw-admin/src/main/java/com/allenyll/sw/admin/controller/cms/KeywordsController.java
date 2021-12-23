package com.allenyll.sw.admin.controller.cms;

import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.system.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.allenyll.sw.system.service.cms.IKeyWordsService;
import com.allenyll.sw.system.service.cms.ISearchHistoryService;
import com.allenyll.sw.system.service.cms.impl.KeywordsServiceImpl;
import com.allenyll.sw.common.entity.cms.Keywords;
import com.allenyll.sw.common.entity.cms.SearchHistory;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(value = "关键字管理", tags = "关键字模块")
@Slf4j
@Controller
@RequestMapping("keywords")
public class KeywordsController extends BaseController<KeywordsServiceImpl, Keywords> {

    @Autowired
    IKeyWordsService keywordsService;

    @Autowired
    ISearchHistoryService searchHistoryService;

    @Override
    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public DataResponse add(@CurrentUser(isFull = true) User user, @RequestBody Keywords keywords) {
        keywords.setId(SnowflakeIdWorker.generateId());
        return super.add(user, keywords);
    }

    @ApiOperation("获取关键词")
    @ResponseBody
    @RequestMapping(value = "getSearchKeyword", method = RequestMethod.POST)
    public Result<Map> getSearchKeyword(@RequestBody Map<String, Object> params){
        return service.getSearchKeyword(params);
    }

    @ApiOperation("获取关键词列表")
    @ResponseBody
    @RequestMapping(value = "getKeywords", method = RequestMethod.POST)
    public DataResponse getKeywords(@RequestBody Map<String, Object> params){
        Map<String, Object> result = new HashedMap();
        String keyword = MapUtil.getString(params, "keyword");
        if (StringUtil.isEmpty(keyword)) {
            return DataResponse.fail("关键字为空，无法查询");
        }
        QueryWrapper<Keywords> wrapper = new QueryWrapper<>();
        wrapper.eq("IS_DELETE", 0);
        wrapper.like("KEYWORD", keyword);
        List<Keywords> keywords = keywordsService.list(wrapper);
        if (keywords != null && keywords.size() > 10) {
            keywords = keywords.subList(0, 10);
        }

        // 新增搜索记录
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setId(SnowflakeIdWorker.generateId());
        searchHistory.setDataSource("小程序");
        searchHistory.setKeyword(keyword);
        searchHistory.setUserId(MapUtil.getLong(params, "customerId"));
        searchHistory.setIsDelete(0);
        searchHistory.setAddTime(DateUtil.getCurrentDateTime());
        searchHistory.setAddUser(MapUtil.getLong(params, "customerId"));
        searchHistory.setUpdateTime(DateUtil.getCurrentDateTime());
        searchHistory.setUpdateUser(MapUtil.getLong(params, "customerId"));
        searchHistoryService.save(searchHistory);

        result.put("keywordList", keywords);

        return DataResponse.success(result);
    }

}
