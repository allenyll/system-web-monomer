package com.allenyll.sw.admin.controller.cms;

import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.exception.BusinessException;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.system.service.product.IGoodsService;
import com.allenyll.sw.system.service.cms.impl.FootprintServiceImpl;
import com.allenyll.sw.common.entity.cms.Footprint;
import com.allenyll.sw.common.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品浏览记录
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2020-11-04 09:48:58
 */
@Slf4j
@Api(value = "商品浏览记录", tags = "商品浏览记录")
@RestController
@RequestMapping("/footprint")
public class FootprintController extends BaseController<FootprintServiceImpl,Footprint> {

    @Autowired
    IGoodsService goodsService;

    @Override
    @ApiOperation("分页查询浏览")
    @RequestMapping(value = "page", method = RequestMethod.GET)
    public DataResponse page(@RequestParam Map<String, Object> params){
        Map<String, Object> result = new HashMap<>();
        int total = service.selectCount(params);
        List<Footprint>  list = service.getFootprintPage(params);
        result.put("total", total);
        result.put("list", list);
        return DataResponse.success(result);
    }

    @ApiOperation("商品浏览记录")
    @ResponseBody
    @RequestMapping(value = "/saveFootprint", method = RequestMethod.POST)
    public Result saveFootprint(@CurrentUser(isFull = true) User user, @RequestBody Map<String, Object> params){
        service.saveFootprint(user, params);
        return new Result();
    }

    @ApiOperation("小程序获取浏览记录")
    @ResponseBody
    @RequestMapping(value = "/getFootprint/{customerId}", method = RequestMethod.POST)
    public Result<List<Footprint>>  getFootprint(@PathVariable Long customerId){
        Result<List<Footprint>>  result = service.getFootprint(customerId);
        return result;
    }

    @ApiOperation("删除商品浏览记录")
    @ResponseBody
    @RequestMapping(value = "/deleteFootprint", method = RequestMethod.POST)
    public Result deleteFootprint(@CurrentUser(isFull = true) User user, @RequestBody Map<String, Object> params){
        Result result = new Result();
        try {
            service.deleteFootprint(user, params);
        } catch (BusinessException e) {
            result.fail(e.getMessage());
        }
        return result;
    }

}
