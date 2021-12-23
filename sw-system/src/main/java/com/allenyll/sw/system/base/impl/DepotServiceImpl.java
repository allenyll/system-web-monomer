package com.allenyll.sw.system.base.impl;

import com.allenyll.sw.common.constants.BaseConstants;
import com.allenyll.sw.common.entity.system.DepotTree;
import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.common.util.StringUtil;
import com.allenyll.sw.common.util.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.common.entity.system.Depot;
import com.allenyll.sw.system.mapper.sys.DepotMapper;
import com.allenyll.sw.system.base.IDepotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author:       allenyll
 * @Date:         2020/8/31 2:59 下午
 * @Version:      1.0
 */
@Slf4j
@Service("depotService")
@Transactional(rollbackFor = Exception.class)
public class DepotServiceImpl extends ServiceImpl<DepotMapper, Depot> implements IDepotService {

    @Autowired
    private DepotMapper depotMapper;

    @Override
    public DataResponse getDepotList(String name) {
        log.info("============= {开始调用方法：getDepotList(} =============");
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<Depot> wrapper = new QueryWrapper<>();
        wrapper.eq("is_delete", 0);
        if(StringUtil.isNotEmpty(name)){
            wrapper.like("depot_name", name);
        }
        List<Depot> depots = depotMapper.selectList(wrapper);

        List<DepotTree> list = getDepotTree(depots, BaseConstants.MENU_ROOT);

        result.put("depots", list);
        log.info("============= {结束调用方法：getDepotList(} =============");
        return DataResponse.success(result);
    }
    
    @Override
    public DataResponse getDepotTree() {
        log.info("==================开始调用getDepotTree================");
        Map<String, Object> result = new HashMap<>();

        QueryWrapper<Depot> wrapper = new QueryWrapper<>();
        wrapper.eq("is_delete", 0);

        List<Depot> list = depotMapper.selectList(wrapper);
        if(!CollectionUtils.isEmpty(list)){
            for(Depot depot:list){
                setParentDepot(depot);
            }
        }

        Depot depot = new Depot();
        depot.setId(0L);
        depot.setDepotName("顶级节点");
        depot.setDepotCode("top");
        depot.setDepotCode("top");
        depot.setIsDelete(0);
        depot.setPid(1000000L);
        list.add(depot);

        List<DepotTree> trees = getDepotTree(list, 1000000L);

        result.put("depotTree", trees);
        log.info("==================结束调用getDepotTree================");
        return DataResponse.success(result);
    }

    @Override
    public DataResponse getDepot(Long id) {
        log.info("==================开始调用 get================");
        DataResponse dataResponse = new DataResponse();
        Map<String, Object> data = new HashMap<>();
        Depot depot = depotMapper.selectById(id);
        if(BaseConstants.MENU_ROOT.equals(id)){
            depot = new Depot();
            depot.setId(id);
            depot.setDepotName("顶级节点");
        }else{
            setParentDepot(depot);
        }

        data.put("obj", depot);
        dataResponse.put("data", data);
        log.info("==================结束调用 get================");
        return dataResponse;
    }

    public void setParentDepot(Depot depot) {
        Long parentId = depot.getPid();
        if(parentId.equals(BaseConstants.MENU_ROOT)){
            depot.setParentDepotName("顶级节点");
        }else{
            QueryWrapper<Depot> entityWrapper = new QueryWrapper<>();
            entityWrapper.eq("is_delete", 0);
            entityWrapper.eq("id", parentId);
            Depot sysDepot = depotMapper.selectOne(entityWrapper);
            if(sysDepot != null){
                depot.setParentDepotName(sysDepot.getDepotName());
            }
        }
    }

    public List<DepotTree> getDepotTree(List<Depot> list, Long rootId) {
        List<DepotTree> trees = new ArrayList<>();
        DepotTree tree;
        if(!CollectionUtils.isEmpty(list)){
            for(Depot obj:list){
                tree = new DepotTree();
                tree.setId(obj.getId());
                tree.setParentId(obj.getPid());
                tree.setName(obj.getDepotName());
                tree.setCode(obj.getDepotCode());
                tree.setTitle(obj.getDepotName());
                tree.setLabel(obj.getDepotName());
                trees.add(tree);
            }
        }
        return TreeUtil.build(trees, rootId);
    }
}
