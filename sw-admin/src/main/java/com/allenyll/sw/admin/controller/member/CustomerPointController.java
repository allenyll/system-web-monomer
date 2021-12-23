package com.allenyll.sw.admin.controller.member;

import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.system.service.member.impl.CustomerPointServiceImpl;
import com.allenyll.sw.system.service.member.impl.CustomerServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.allenyll.sw.common.entity.customer.Customer;
import com.allenyll.sw.common.entity.customer.CustomerPoint;
import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.common.util.MapUtil;
import com.allenyll.sw.common.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yu.leilei
 * @since 2019-01-09
 */
@Slf4j
@RestController
@RequestMapping("customerPoint")
@Api(value = "用户积分相关接口")
public class CustomerPointController extends BaseController<CustomerPointServiceImpl, CustomerPoint> {

    @Autowired
    CustomerServiceImpl customerService;

    @ApiOperation("分页查询积分")
    @Override
    @ResponseBody
    @RequestMapping(value = "page", method = RequestMethod.GET)
    public DataResponse page(@RequestParam Map<String, Object> params) {
        String customerAccount = MapUtil.getMapValue(params, "customerAccount");
        if(StringUtil.isNotEmpty(customerAccount)){
            QueryWrapper<Customer> wrapper = new QueryWrapper<>();
            wrapper.eq("CUSTOMER_ACCOUNT", customerAccount);
            Customer customer = customerService.getOne(wrapper);
            if(customer != null){
                params.put("eq_customer_id", customer.getId());
            }else{
                params.put("eq_customer_id", "undefined");
            }
        }

        DataResponse response = super.page(params);
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        List<CustomerPoint> list = (List<CustomerPoint>) data.get("list");
        if(!CollectionUtils.isEmpty(list)){
            for(CustomerPoint customerPoint:list){
                Customer customer = customerService.getById(customerPoint.getCustomerId());
                if(customer != null){
                    customerPoint.setCustomerName(customer.getCustomerName());
                    customerPoint.setCustomerAccount(customer.getCustomerAccount());
                }
            }
        }

        response.put("list", list);

        return response;
    }

    /**
     * 获取积分
     * @param param
     * @return
     */
    @ApiOperation("获取积分")
    @ResponseBody
    @RequestMapping(value = "/getPoint", method = RequestMethod.POST)
    public DataResponse getPoint(@RequestBody Map<String, Object> param){
        log.info("==============开始调用getPoint================");
        return service.getPotint(param);
    }

    /**
     * 获取积分详情
     * @param param
     * @return
     */
    @ApiOperation("获取积分详情")
    @ResponseBody
    @RequestMapping(value = "/getPointDetail", method = RequestMethod.POST)
    public DataResponse getPointDetail(@RequestBody Map<String, Object> param){
        log.info("==============开始调用 getPointDetail ================");
        return service.getPointDetail(param);
    }

    @ResponseBody
    @RequestMapping(value = "/selectOne", method = RequestMethod.POST)
    public CustomerPoint selectOne(@RequestBody Map<String, Object> map) {
        QueryWrapper<CustomerPoint> customerPointEntityWrapper = new QueryWrapper<>();
        customerPointEntityWrapper.eq("IS_DELETE", 0);
        customerPointEntityWrapper.eq("CUSTOMER_ID", MapUtil.getLong(map, "CUSTOMER_ID"));
        return service.getOne(customerPointEntityWrapper);
    }

}
