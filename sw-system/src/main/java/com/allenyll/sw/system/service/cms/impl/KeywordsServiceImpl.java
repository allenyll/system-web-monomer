package com.allenyll.sw.system.service.cms.impl;

import com.allenyll.sw.common.util.*;
import com.allenyll.sw.system.service.cms.ISearchHistoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.system.mapper.cms.KeywordsMapper;
import com.allenyll.sw.system.service.cms.IKeyWordsService;
import com.allenyll.sw.common.entity.cms.Keywords;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 热闹关键词表
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-12-27 14:46:03
 */
@Service("keywordsService")
public class KeywordsServiceImpl extends ServiceImpl<KeywordsMapper, Keywords> implements com.allenyll.sw.system.service.cms.IKeyWordsService {

    @Autowired
    KeywordsMapper keywordsMapper;

    @Autowired
    IKeyWordsService keywordsService;

    @Autowired
    ISearchHistoryService searchHistoryService;

    @Override
    public List<Map<String, String>> selectHotKeywordList() {
        return keywordsMapper.selectHotKeywordList();
    }

    @Override
    public Result<Map> getSearchKeyword(Map<String, Object> params) {
        Map<String, Object> returnMap = new HashedMap();
        Result<Map> result = new Result<>();
        QueryWrapper<Keywords> wrapper = new QueryWrapper<>();
        wrapper.eq("IS_DELETE", 0);
        wrapper.eq("IS_DEFAULT", "SW1001");
        List<Keywords> isDefaultKeywords = keywordsService.list(wrapper);
        Keywords keywords = new Keywords();
        if (CollectionUtil.isNotEmpty(isDefaultKeywords)) {
            keywords = isDefaultKeywords.get(0);
        }
        List<Map<String, String>> isHotKeyWords = keywordsService.selectHotKeywordList();
        returnMap.put("isDefaultKeyWords", keywords);
        returnMap.put("isHotKeywords", isHotKeyWords);

        String customerId = MapUtil.getString(params, "userId");
        List<Map<String, String>> historyKeywords = null;

        if (StringUtil.isEmpty(customerId)) {
            historyKeywords = new ArrayList<>();
            returnMap.put("historyKeywords", historyKeywords);
            result.setData(returnMap);
            return result;
        }
        historyKeywords = searchHistoryService.selectHistoryKeywordList(params);
        returnMap.put("historyKeywords", historyKeywords);
        result.setData(returnMap);
        return result;
    }

}
