package com.allenyll.sw.system.service.cms;

import com.allenyll.sw.common.util.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import com.allenyll.sw.common.entity.cms.Keywords;

import java.util.List;
import java.util.Map;

public interface IKeyWordsService extends IService<Keywords> {

    /**
     * 查询热门搜索列表
     * @return
     */
    List<Map<String, String>> selectHotKeywordList();

    /**
     * 获取关键词
     * @param params
     * @return
     */
    Result<Map> getSearchKeyword(Map<String, Object> params);
}
