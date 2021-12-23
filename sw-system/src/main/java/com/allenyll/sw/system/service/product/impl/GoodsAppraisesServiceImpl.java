package com.allenyll.sw.system.service.product.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.system.mapper.product.GoodsAppraisesMapper;
import com.allenyll.sw.system.service.product.IGoodsAppraisesService;
import com.allenyll.sw.common.entity.product.GoodsAppraises;
import org.springframework.stereotype.Service;

/**
 * 商品评价表
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-05-09 15:16:31
 */
@Service("goodsAppraisesService")
public class GoodsAppraisesServiceImpl extends ServiceImpl<GoodsAppraisesMapper, GoodsAppraises> implements IGoodsAppraisesService {

}
