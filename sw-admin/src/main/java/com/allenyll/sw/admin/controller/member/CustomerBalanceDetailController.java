package com.allenyll.sw.admin.controller.member;

import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.system.service.member.impl.CustomerBalanceDetailServiceImpl;
import com.allenyll.sw.common.entity.customer.CustomerBalanceDetail;
import com.allenyll.sw.common.util.DataResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Description:  月明细管理
 * @Author:       allenyll
 * @Date:         2020/5/26 10:32 下午
 * @Version:      1.0
 */
@Slf4j
@Api("余额明细接口")
@RestController
@RequestMapping("customerBalanceDetail")
public class CustomerBalanceDetailController extends BaseController<CustomerBalanceDetailServiceImpl, CustomerBalanceDetail> {
    

    /**
     * 获取积分详情
     * @param param
     * @return
     */
    @ApiOperation("微信余额详细")
    @ResponseBody
    @RequestMapping(value = "/getBalanceDetail", method = RequestMethod.POST)
    public DataResponse getDetail(@RequestBody Map<String, Object> param){
        log.info("==============开始调用 getBalanceDetail ================");
        return service.getBalanceDetail(param);
    }

}
