package com.allenyll.sw.system.mapper.product;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.allenyll.sw.common.dto.GoodsQueryDto;
import com.allenyll.sw.common.dto.GoodsResult;
import com.allenyll.sw.common.entity.product.Goods;

/**
 * 商品基本信息表
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-03-21 10:51:24
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 获取库存信息
     * @param goodsQueryDto
     * @return
     */
    GoodsResult getStock(GoodsQueryDto goodsQueryDto);

    /**
     * 获取告警库存商品数量
     * @param goodsQueryDto
     * @return
     */
    int getWarnStock(GoodsQueryDto goodsQueryDto);
}
