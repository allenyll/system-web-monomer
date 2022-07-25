package com.allenyll.sw.admin.controller.product;

import com.allenyll.sw.system.service.product.impl.SpecOptionServiceImpl;
import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.common.dto.SpecsDto;
import com.allenyll.sw.common.entity.product.SpecOption;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.CollectionUtil;
import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.common.util.Result;
import com.allenyll.sw.common.util.SnowflakeIdWorker;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "规格选项接口", tags = "规格选项管理")
@Controller
@RequestMapping("specOption")
public class SpecOptionController extends BaseController<SpecOptionServiceImpl, SpecOption> {

    @ApiOperation("新增规格选项")
    @Override
    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public DataResponse add(@CurrentUser(isFull = true) User user, @RequestBody SpecOption entity) {
        entity.setId(SnowflakeIdWorker.generateId());
        String maxCode = service.getMaxCode(String.valueOf(entity.getSpecsId()));
        entity.setCode(maxCode);
        return super.add(user, entity);
    }

    @ApiOperation("封装前端下拉列表")
    @Override
    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public DataResponse list() {
        DataResponse dataResponse = super.list();
        Map<String, Object> data = (Map<String, Object>) dataResponse.get("data");
        List<SpecOption> list = (List<SpecOption>) data.get("list");
        Map<Long, String> map = new HashMap<>();
        List<Map<String, Object>> newList = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(list)){
            for(SpecOption specOption:list){
                Map<String, Object> _map = new HashMap<>();
                map.put(specOption.getId(), specOption.getName());
                _map.put("label", specOption.getName());
                _map.put("value", specOption.getId());
                newList.add(_map);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("map", map);
        result.put("list", newList);
        return DataResponse.success(result);
    }

    @ApiOperation("根据规格获取规格选项列表")
    @PostMapping("getSpecsOptionBySpecsId")
    @ResponseBody
    public Result<List<SpecOption>> getSpecsOptionBySpecsId(@RequestBody SpecsDto specsDto) {
        return service.getSpecsOptionBySpecsId(specsDto);
    }


}
