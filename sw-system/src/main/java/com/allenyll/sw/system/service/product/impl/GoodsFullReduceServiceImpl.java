package com.allenyll.sw.system.service.product.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.system.mapper.product.GoodsFullReduceMapper;
import com.allenyll.sw.system.service.product.IGoodsFullReduceService;
import com.allenyll.sw.common.entity.product.GoodsFullReduce;
import org.springframework.stereotype.Service;

/**
 * 商品满减
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-05-28 17:24:36
 */
@Service("goodsFullReduceService")
public class GoodsFullReduceServiceImpl extends ServiceImpl<GoodsFullReduceMapper, GoodsFullReduce> implements IGoodsFullReduceService {

}
