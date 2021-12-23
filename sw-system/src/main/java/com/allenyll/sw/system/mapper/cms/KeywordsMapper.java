package com.allenyll.sw.system.mapper.cms;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.allenyll.sw.common.entity.cms.Keywords;

import java.util.List;
import java.util.Map;

/**
 * 热闹关键词表
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-12-27 14:46:03
 */
public interface KeywordsMapper extends BaseMapper<Keywords> {

    List<Map<String, String>> selectHotKeywordList();
}
