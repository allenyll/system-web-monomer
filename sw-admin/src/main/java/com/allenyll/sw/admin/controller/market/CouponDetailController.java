package com.allenyll.sw.admin.controller.market;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.allenyll.sw.system.service.market.impl.CouponDetailServiceImpl;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.common.entity.market.CouponDetail;
import com.allenyll.sw.common.util.MapUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author yuleilei
 */
@Api(tags = "优惠券明细管理")
@RestController
@RequestMapping("couponDetail")
public class CouponDetailController extends BaseController<CouponDetailServiceImpl, CouponDetail> {

    @ApiOperation("获取优惠券明细列表")
    @RequestMapping(value = "getCouponDetailList", method = RequestMethod.POST)
    public List<CouponDetail> getCouponDetailList(@RequestBody Map<String, Object> param) {
        QueryWrapper<CouponDetail> couponDetailEntityWrapper = new QueryWrapper<>();
        couponDetailEntityWrapper.eq("IS_DELETE", 0);
        couponDetailEntityWrapper.eq("COUPON_ID", MapUtil.getLong(param, "COUPON_ID"));
        return service.list(couponDetailEntityWrapper);
    }

    @ApiOperation("根据ID更新优惠券明细")
    @RequestMapping(value = "updateById", method = RequestMethod.POST)
    public void updateById(@RequestBody CouponDetail couponDetail) {
        service.updateById(couponDetail);
    }

}
