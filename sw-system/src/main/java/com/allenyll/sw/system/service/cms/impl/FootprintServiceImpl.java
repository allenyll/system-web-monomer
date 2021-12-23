package com.allenyll.sw.system.service.cms.impl;

import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.enums.dict.StatusDict;
import com.allenyll.sw.common.exception.BusinessException;
import com.allenyll.sw.common.util.*;
import com.allenyll.sw.system.service.product.IGoodsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.system.mapper.cms.FootprintMapper;
import com.allenyll.sw.system.service.cms.IFootprintService;
import com.allenyll.sw.common.entity.cms.Footprint;
import com.allenyll.sw.common.entity.product.Goods;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service("footprintService")
public class FootprintServiceImpl extends ServiceImpl<FootprintMapper,Footprint> implements com.allenyll.sw.system.service.cms.IFootprintService {

    @Autowired
    private FootprintMapper footprintMapper;

    @Autowired
    private IGoodsService goodsService;

    @Override
    public Result<List<Footprint>>  getFootprint(Long customerId) {
        Result<List<Footprint>> result = new Result<>();
        QueryWrapper<Footprint> wrapper = new QueryWrapper<>();
        wrapper.eq("add_user", customerId);
        wrapper.eq("is_delete", 0);
        wrapper.eq("type", "商品");
        List<Footprint> isFootPrint = footprintMapper.selectList(wrapper);
        if (CollectionUtil.isNotEmpty(isFootPrint)) {
            for (Footprint footprint:isFootPrint) {
                Long goodsId = footprint.getGoodsId();
                Goods goods = goodsService.getById(goodsId);
                if (goods == null) {
                    continue;
                }
                goodsService.setFile(goods);
                footprint.setGoods(goods);
            }
        }
        result.setData(isFootPrint);
        return result;
    }

    @Override
    public int selectCount(Map<String, Object> params) {
        Integer num = footprintMapper.selectCount(params);
        return num == null ? 0 : num.intValue();
    }

    @Override
    public List<Footprint> getFootprintPage(Map<String, Object> params) {
        int page = Integer.parseInt(params.get("page").toString());
        int limit = Integer.parseInt(params.get("limit").toString());
        int start = (page - 1) * limit;
        params.put("start", start);
        params.put("limit", limit);
        List<Footprint> list = footprintMapper.getFootprintPage(params);
        return list;
    }

    @Override
    public void saveFootprint(User user, Map<String, Object> params) {
        String type = MapUtil.getString(params, "type");
        Long goodsId = MapUtil.getLong(params, "goodsId");
        Long customerId = MapUtil.getLong(params, "customerId");
        if (goodsId == null || customerId == null) {
            log.warn("用户：{}, 添加商品浏览记录参数不完整", user.getAccount());
            return;
        }
        QueryWrapper<Footprint> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id", goodsId);
        wrapper.eq("add_user", customerId);
        wrapper.eq("is_delete", 0);
        wrapper.eq("type", type);
        Footprint isFootPrint = footprintMapper.selectOne(wrapper);
        if (isFootPrint != null) {
            isFootPrint.setUpdateUser(customerId);
            isFootPrint.setTimes(isFootPrint.getTimes()+1);
            isFootPrint.setUpdateTime(DateUtil.getCurrentDateTime());
            footprintMapper.updateById(isFootPrint);
        } else {
            Footprint footprint = new Footprint();
            footprint.setId(SnowflakeIdWorker.generateId());
            footprint.setType(type);
            footprint.setGoodsId(goodsId);
            footprint.setStatus(StatusDict.START.getCode());
            footprint.setTimes(1);
            footprint.setIsDelete(0);
            footprint.setAddUser(customerId);
            footprint.setAddTime(DateUtil.getCurrentDateTime());
            footprint.setUpdateUser(customerId);
            footprint.setUpdateTime(DateUtil.getCurrentDateTime());
            footprintMapper.insert(footprint);
        }
    }

    @Override
    public void deleteFootprint(User user, Map<String, Object> params) {
        Long goodsId = MapUtil.getLong(params, "goodsId");
        Long customerId = MapUtil.getLong(params, "customerId");
        String type = MapUtil.getString(params, "type");
        QueryWrapper<Footprint> wrapper = new QueryWrapper<>();
        wrapper.eq("goods_id", goodsId);
        wrapper.eq("add_user", customerId);
        wrapper.eq("is_delete", 0);
        wrapper.eq("type", type);
        if (goodsId == null || customerId == null) {
            log.warn("用户：{}, 删除商品浏览记录参数不完整", user.getAccount());
            throw new BusinessException("删除商品浏览记录参数不完整");
        }
        Footprint isFootPrint = footprintMapper.selectOne(wrapper);
        if (isFootPrint != null) {
            isFootPrint.setUpdateUser(customerId);
            isFootPrint.setIsDelete(1);
            isFootPrint.setUpdateTime(DateUtil.getCurrentDateTime());
            footprintMapper.updateById(isFootPrint);
        }
    }
}
