package com.allenyll.sw.system.base.impl;

import com.allenyll.sw.common.enums.dict.StatusDict;
import com.allenyll.sw.common.util.DataResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.common.entity.system.Dict;
import com.allenyll.sw.common.util.CollectionUtil;
import com.allenyll.sw.system.mapper.sys.DictMapper;
import com.allenyll.sw.system.base.IDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 字典表
 服务实现类
 * </p>
 *
 * @author yu.leilei
 * @since 2019-02-15
 */
@Slf4j
@Service("dictService")
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements IDictService {

   @Resource
    DictMapper dictMapper;

    public List<Dict> selectList(QueryWrapper<Dict> wrapper) {
        return dictMapper.selectList(wrapper);
    }

    @Override
    public Dict getDictByCode(String code){
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("IS_DELETE", 0);
        wrapper.eq("CODE", code);
        List<Dict> dictList = dictMapper.selectList(wrapper);
        if(CollectionUtil.isNotEmpty(dictList)){
            return dictList.get(0);
        }
        return null;
    }

    @Override
    public DataResponse getDictList(String code) {
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("is_delete", 0);
        wrapper.eq("status", StatusDict.START.getCode());
        wrapper.like("code", code);
        wrapper.ne("parent_id",0);
        List<Dict> list = dictMapper.selectList(wrapper);

        Map<String, String> map = new HashMap<>();
        List<Map<String, String>> newList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(list)){
            for(Dict dict:list){
                Map<String, String> _map = new HashMap<>();
                map.put(dict.getCode(), dict.getName());
                _map.put("label", dict.getName());
                _map.put("value", dict.getCode());
                newList.add(_map);
            }
        }
        result.put("map", map);
        result.put("list", newList);
        return DataResponse.success(result);
    }

    @Override
    public DataResponse getParent() {
        log.info("============= {开始调用方法：getParentDict(} =============");
        Map<String, Object> result = new HashMap<>();

        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("is_delete", 0);
        wrapper.orderBy( true, false, "CODE");
        wrapper.eq("parent_id", 0);
        List<Dict> list = dictMapper.selectList(wrapper);
        result.put("list", list);
        return DataResponse.success(result);
    }

    @Override
    public DataResponse getChild(String id) {
        log.info("============= {开始调用方法：DICT getChild(} =============");
        Map<String, Object> result = new HashMap<>();

        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("is_delete", 0);
        wrapper.orderBy(true, false, "CODE");
        wrapper.eq("parent_id", id);
        List<Dict> list = dictMapper.selectList(wrapper);

        result.put("list", list);
        return DataResponse.success(result);
    }
}
