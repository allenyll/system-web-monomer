package com.allenyll.sw.system.service.product.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.system.mapper.product.BrandMapper;
import com.allenyll.sw.system.service.product.IBrandService;
import com.allenyll.sw.common.entity.product.Brand;
import org.springframework.stereotype.Service;

/**
 * 商品品牌
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-03-21 10:04:09
 */
@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements com.allenyll.sw.system.service.product.IBrandService {

}
