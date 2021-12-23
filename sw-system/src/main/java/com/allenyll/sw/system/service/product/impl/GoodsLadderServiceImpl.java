package com.allenyll.sw.system.service.product.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.system.mapper.product.GoodsLadderMapper;
import com.allenyll.sw.system.service.product.IGoodsLadderService;
import com.allenyll.sw.common.entity.product.GoodsLadder;
import org.springframework.stereotype.Service;

/**
 * 商品阶梯价格关联
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-05-28 17:24:48
 */
@Service("goodsLadderService")
public class GoodsLadderServiceImpl extends ServiceImpl<GoodsLadderMapper, GoodsLadder> implements com.allenyll.sw.system.service.product.IGoodsLadderService {

}
