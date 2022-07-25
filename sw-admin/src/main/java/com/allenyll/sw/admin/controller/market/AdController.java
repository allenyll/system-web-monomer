package com.allenyll.sw.admin.controller.market;

import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.common.entity.market.Message;
import com.allenyll.sw.common.enums.dict.StatusDict;
import com.allenyll.sw.common.util.*;
import com.allenyll.sw.system.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.allenyll.sw.system.service.market.impl.AdServiceImpl;
import com.allenyll.sw.common.entity.market.Ad;
import com.allenyll.sw.common.entity.system.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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

@Api(tags = "广告管理")
@Controller
@RequestMapping("ad")
public class AdController extends BaseController<AdServiceImpl, Ad> {

    @Autowired
    AdServiceImpl adService;

    @Override
    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public DataResponse add(@CurrentUser(isFull = true) User user, @RequestBody Ad ad) {
        ad.setId(SnowflakeIdWorker.generateId());
        return super.add(user, ad);
    }

    @ApiOperation("广告列表(前台数据展示转换)")
    @Override
    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public DataResponse list() {
        DataResponse dataResponse = super.list();
        Map<String, Object> data = (Map<String, Object>) dataResponse.get("data");
        List<Ad> list = (List<Ad>) data.get("list");
        Map<Long, String> map = new HashMap<>();
        List<Map<String, Object>> newList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(list)){
            for(Ad Ad:list){
                Map<String, Object> _map = new HashMap<>();
                map.put(Ad.getId(), Ad.getAdName());
                _map.put("label", Ad.getAdName());
                _map.put("value", Ad.getId());
                newList.add(_map);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("map", map);
        result.put("list", newList);
        return DataResponse.success(result);
    }

    @ApiOperation("广告列表")
    @ResponseBody
    @RequestMapping(value = "getAdList", method = RequestMethod.POST)
    public Result<List<Ad>> getAdList(@RequestBody Map<String, Object> params) {
        List<Ad> adList = service.getAdList(params);
        Result<List<Ad>> result = new Result<>();
        result.setData(adList);
        return result;
    }

}
