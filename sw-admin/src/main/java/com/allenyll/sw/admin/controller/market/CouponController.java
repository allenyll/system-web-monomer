package com.allenyll.sw.admin.controller.market;

import com.allenyll.sw.common.enums.dict.CouponDict;
import com.allenyll.sw.common.exception.BusinessException;
import com.allenyll.sw.common.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.allenyll.sw.system.service.market.impl.CouponServiceImpl;
import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.common.entity.market.Coupon;
import com.allenyll.sw.common.entity.system.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags = "优惠券管理")
@RestController
@RequestMapping("coupon")
public class CouponController extends BaseController<CouponServiceImpl, Coupon> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CouponController.class);

    @Autowired
    CouponServiceImpl couponService;

    @Override
    @ApiOperation("添加优惠券")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public DataResponse add(@CurrentUser(isFull = true) User user, @RequestBody Coupon coupon){
        LOGGER.info("coupon: " + coupon);
        Long id = SnowflakeIdWorker.generateId();
        coupon.setId(id);
        coupon.setReceiveCount(0);
        coupon.setUseCount(0);
        super.add(user, coupon);
        List<Map<String, Object>> list = coupon.getCouponGoodsList();
        if(CollectionUtil.isNotEmpty(list)){
            for(Map<String, Object> map:list){
                map.put("id", StringUtil.getUUID32());
                map.put("couponId", id);
                couponService.addCouponGoods(map);
            }
        }
        return DataResponse.success();
    }

    @ApiOperation("根据ID获取优惠券")
    @Override
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public DataResponse get(@PathVariable Long id){
        Map<String, Object> result = new HashMap<>();

        Coupon obj =  couponService.getById(id);

        if(CouponDict.GOODS.getCode().equals(obj.getUseType())){
            List<Map<String, Object>> list = couponService.selectCouponGoods(obj.getId());
            obj.setCouponGoodsList(list);
        }
        result.put("obj", obj);
        return DataResponse.success(result);
    }

    @ApiOperation("发放优惠券")
    @RequestMapping(value = "publishCoupon", method = RequestMethod.POST)
    public DataResponse publishCoupon(@RequestBody Map<String, Object> params){
        DataResponse dataResponse = couponService.publishCoupon(params);
        return dataResponse;
    }

    @ApiOperation("调度任务获取所有未被删除的优惠券")
    @RequestMapping(value = "getCoupons", method = RequestMethod.POST)
    public List<Coupon> getCoupons(@RequestBody Map<String, Object> param) {
        QueryWrapper<Coupon> couponEntityWrapper = new QueryWrapper<>();
        couponEntityWrapper.eq("is_delete", 0);
        return service.list(couponEntityWrapper);
    }

    @ApiOperation("[小程序接口]获取优惠券列表")
    @RequestMapping(value = "getCouponList", method = RequestMethod.POST)
    public Result getCouponList(@RequestBody Map<String, Object> params){
        Result result = new Result<>();
        Map<String, Object> data;
        try {
            data = couponService.getCouponList(params);
        } catch (BusinessException e) {
            log.error(e.getMessage());
            result.fail(e.getMessage());
            return result;
        }
        result.setData(data);
        return result;
    }

}
