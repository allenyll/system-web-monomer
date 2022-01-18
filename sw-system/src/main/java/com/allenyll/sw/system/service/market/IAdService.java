package com.allenyll.sw.system.service.market;

import com.allenyll.sw.common.util.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import com.allenyll.sw.common.entity.market.Ad;

import java.util.List;
import java.util.Map;

public interface IAdService extends IService<Ad> {
    
    /**
     * 广告列表
     * @param params
     * @return
     */
    List<Ad> getAdList(Map<String, Object> params);
}
