package com.allenyll.sw.job.schedule.coupon;

import com.allenyll.sw.common.entity.market.Coupon;
import com.allenyll.sw.common.entity.market.CouponDetail;
import com.allenyll.sw.common.enums.dict.CouponDict;
import com.allenyll.sw.common.enums.dict.IsOrNoDict;
import com.allenyll.sw.common.util.CollectionUtil;
import com.allenyll.sw.common.util.DateUtil;
import com.allenyll.sw.job.service.IJobLogService;
import com.allenyll.sw.system.service.market.impl.CouponDetailServiceImpl;
import com.allenyll.sw.system.service.market.impl.CouponServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:  优惠券过期
 * @Author:       allenyll
 * @Date:         2019-07-15 12:07
 * @Version:      1.0
 */
@Slf4j
@Component
public class CouponJob implements Job {

    @Autowired
    CouponServiceImpl couponService;
    
    @Autowired
    CouponDetailServiceImpl couponDetailService;

    @Autowired
    IJobLogService jobLogService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Long startTime = System.currentTimeMillis();
        String errMsg = "";
        String status = IsOrNoDict.YES.getCode();
        try {
            Map<String, Object> param = new HashMap<>();
            QueryWrapper<Coupon> couponEntityWrapper = new QueryWrapper<>();
            couponEntityWrapper.eq("is_delete", 0);
            List<Coupon> list = couponService.list(couponEntityWrapper);
            if(CollectionUtil.isNotEmpty(list)){
                for(Coupon coupon:list){
                    String time = DateUtil.getCurrentDate();
                    // 优惠券已过期
                    if(time.compareTo(coupon.getEndTime()) > 0){
                        QueryWrapper<CouponDetail> couponDetailEntityWrapper = new QueryWrapper<>();
                        couponDetailEntityWrapper.eq("IS_DELETE", 0);
                        couponDetailEntityWrapper.eq("COUPON_ID", coupon.getId());
                        List<CouponDetail> couponDetails = couponDetailService.list(couponDetailEntityWrapper);
                        if(CollectionUtil.isNotEmpty(couponDetails)){
                            for(CouponDetail couponDetail:couponDetails){
                                if(couponDetail.getUseStatus().equals(CouponDict.UN_USE.getCode())){
                                    couponDetail.setUseStatus(CouponDict.EXPIRE.getCode());
                                    couponDetailService.updateById(couponDetail);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
             log.info("优惠券调度执行失败！", e.getMessage());
             errMsg = e.getMessage();
             status = IsOrNoDict.NO.getCode();
        }
        Long endTime = System.currentTimeMillis();
        jobLogService.saveJobLog(status, errMsg, endTime-startTime, jobExecutionContext.getJobDetail().getJobDataMap());
    }



}
