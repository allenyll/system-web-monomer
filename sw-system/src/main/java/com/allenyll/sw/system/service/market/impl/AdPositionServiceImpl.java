package com.allenyll.sw.system.service.market.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.system.mapper.market.AdPositionMapper;
import com.allenyll.sw.system.service.market.IAdPositionService;
import com.allenyll.sw.common.entity.market.AdPosition;
import org.springframework.stereotype.Service;

/**
 * 广告位表
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-12-19 20:12:58
 */
@Service("adPositionService")
public class AdPositionServiceImpl extends ServiceImpl<AdPositionMapper, AdPosition> implements IAdPositionService {

}
