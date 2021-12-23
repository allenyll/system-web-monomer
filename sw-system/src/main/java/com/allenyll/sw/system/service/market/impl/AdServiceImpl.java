package com.allenyll.sw.system.service.market.impl;

import com.allenyll.sw.common.enums.dict.StatusDict;
import com.allenyll.sw.common.util.DateUtil;
import com.allenyll.sw.common.util.MapUtil;
import com.allenyll.sw.common.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.system.mapper.market.AdMapper;
import com.allenyll.sw.common.entity.market.Ad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-12-19 20:14:24
 */
@Service("adService")
public class AdServiceImpl extends ServiceImpl<AdMapper, Ad> implements com.allenyll.sw.system.service.market.IAdService {

    @Autowired
    private AdMapper adMapper;
    
    @Override
    public Result<List<Ad>> getAdList(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();
        String adType = MapUtil.getString(params, "adType");
        String time = DateUtil.getCurrentDateTime();
        QueryWrapper<Ad> wrapper = new QueryWrapper<>();
        wrapper.eq("AD_TYPE", adType);
        wrapper.eq("IS_DELETE", 0);
        wrapper.eq("IS_USED", StatusDict.START.getCode());
        wrapper.gt("END_TIME", time);
        wrapper.lt("START_TIME", time);
        List<Ad> ads = adMapper.selectList(wrapper);
        Result<List<Ad>> result = new Result<>();
        result.setData(ads);
        return result;
    }
}
