package com.allenyll.sw.admin.controller.product;

import com.allenyll.sw.system.service.product.impl.SpecsGroupServiceImpl;
import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.common.entity.product.SpecsGroup;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.CollectionUtil;
import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.common.util.SnowflakeIdWorker;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "规格组管理")
@Controller
@RequestMapping("specsGroup")
public class SpecsGroupController extends BaseController<SpecsGroupServiceImpl, SpecsGroup> {

    @Override
    @ResponseBody
    @ApiOperation("添加规格组")
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public DataResponse add(@CurrentUser(isFull = true) User user, @RequestBody SpecsGroup entity) {
        entity.setId(SnowflakeIdWorker.generateId());
        return super.add(user, entity);
    }

    @Override
    @ResponseBody
    @ApiOperation("封装规格组，前端使用")
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public DataResponse list() {
        DataResponse dataResponse = super.list();
        Map<String, Object> data = (Map<String, Object>) dataResponse.get("data");
        List<SpecsGroup> list = (List<SpecsGroup>) data.get("list");
        Map<Long, String> map = new HashMap<>();
        List<Map<String, Object>> newList = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(list)){
            for(SpecsGroup specsGroup:list){
                Map<String, Object> _map = new HashMap<>();
                map.put(specsGroup.getId(), specsGroup.getName());
                _map.put("label", specsGroup.getName());
                _map.put("value", specsGroup.getId());
                newList.add(_map);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("map", map);
        result.put("list", newList);
        return DataResponse.success(result);
    }

}
