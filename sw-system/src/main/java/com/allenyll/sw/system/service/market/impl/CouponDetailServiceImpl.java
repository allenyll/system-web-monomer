package com.allenyll.sw.system.service.market.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.system.mapper.market.CouponDetailMapper;
import com.allenyll.sw.system.service.market.ICouponDetailService;
import com.allenyll.sw.common.entity.market.CouponDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 优惠券领取详情表
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-06-29 22:31:34
 */
@Service("couponDetailService")
public class CouponDetailServiceImpl extends ServiceImpl<CouponDetailMapper, CouponDetail> implements ICouponDetailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CouponDetailServiceImpl.class);
}
