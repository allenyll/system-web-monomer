package com.allenyll.sw.admin.controller.product;

import com.allenyll.sw.system.service.product.impl.BrandServiceImpl;
import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.common.entity.product.Brand;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.common.util.SnowflakeIdWorker;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuleilei
 */
@Api(value = "品牌接口", tags = "品牌管理")
@Controller
@RequestMapping("brand")
public class BrandController extends BaseController<BrandServiceImpl, Brand> {

    @Override
    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public DataResponse add(@CurrentUser(isFull = true) User user, @RequestBody Brand entity) {
        entity.setId(SnowflakeIdWorker.generateId());
        return super.add(user, entity);
    }

    @ApiOperation("品牌列表")
    @Override
    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public DataResponse list() {
        DataResponse dataResponse = super.list();
        Map<String, Object> data = (Map<String, Object>) dataResponse.get("data");
        List<Brand> list = (List<Brand>) data.get("list");
        Map<Long, String> map = new HashMap<>();
        List<Map<String, Object>> newList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(list)){
            for(Brand brand:list){
                Map<String, Object> _map = new HashMap<>();
                map.put(brand.getId(), brand.getBrandName());
                _map.put("label", brand.getBrandName());
                _map.put("value", brand.getId());
                newList.add(_map);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("map", map);
        result.put("list", newList);
        return DataResponse.success(result);
    }

}
