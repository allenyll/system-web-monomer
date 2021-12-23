package com.allenyll.sw.system.base.impl;

import com.allenyll.sw.common.constants.BaseConstants;
import com.allenyll.sw.common.entity.system.MenuTree;
import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.common.util.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.common.entity.system.Menu;
import com.allenyll.sw.system.mapper.sys.MenuMapper;
import com.allenyll.sw.system.base.IMenuService;
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
 * @Date:         2020/8/31 3:00 下午
 * @Version:      1.0
 */
@Slf4j
@Service("menuService")
@Transactional(rollbackFor = Exception.class)
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {
    
    @Autowired
    private MenuMapper menuMapper;

    @Override
    public DataResponse getMenuById(Long id) {
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.eq("is_delete", 0);
        wrapper.eq("id", id);

        Menu sysMenu = menuMapper.selectOne(wrapper);
        if(id.equals(BaseConstants.MENU_ROOT)){
            sysMenu = new Menu();
            sysMenu.setId(id);
            sysMenu.setMenuName("顶级节点");
        }else{
            setParentMenu(sysMenu);
        }
        result.put("obj", sysMenu);
        return DataResponse.success(result);
    }

    @Override
    public DataResponse getMenuTreeList(String type) {
        log.info("==================开始调用getMenuTree================");
        Map<String, Object> result = new HashMap<>();

        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.eq("is_delete", 0);
        if ("menu".equals(type)) {
            wrapper.eq("menu_type", "SW0101");
        }

        List<Menu> menuList = menuMapper.selectList(wrapper);
        if(!CollectionUtils.isEmpty(menuList)){
            for(Menu menu:menuList){
                setParentMenu(menu);
            }
        }
        Menu topMenu = new Menu();
        topMenu.setId(0L);
        topMenu.setIsDelete(0);
        topMenu.setMenuName("顶级节点");
        topMenu.setMenuCode("top");
        topMenu.setPid(1000000L);
        topMenu.setMenuIcon("sw-top");
        menuList.add(topMenu);
        List<MenuTree> menuTrees = getMenuTree(menuList, 1000000L);
        result.put("menuTree", menuTrees);
        log.info("==================结束调用getMenuTree================");
        return DataResponse.success(result);
    }

    public List<MenuTree> getMenuTree(List<Menu> menuList, Long menuRootId) {
        List<MenuTree> menuTrees = new ArrayList<>();
        MenuTree menuTree;
        if(!CollectionUtils.isEmpty(menuList)){
            for(Menu menu:menuList){
                menuTree = new MenuTree();
                menuTree.setId(menu.getId());
                menuTree.setParentId(menu.getPid());
                menuTree.setCode(menu.getMenuCode());
                menuTree.setName(menu.getMenuName());
                menuTree.setHref(menu.getMenuUrl());
                menuTree.setTitle(menu.getMenuName());
                menuTree.setLabel(menu.getMenuName());
                menuTree.setIcon(menu.getMenuIcon());
                menuTrees.add(menuTree);
            }
        }
        return TreeUtil.build(menuTrees, menuRootId);
    }

    public void setParentMenu(Menu sysMenu) {
        Long parentId = sysMenu.getPid();
        if (parentId.equals(BaseConstants.MENU_ROOT)) {
            sysMenu.setParentMenuName("顶级节点");
        } else {
            QueryWrapper<Menu> entityWrapper = new QueryWrapper<>();
            entityWrapper.eq("is_delete", 0);
            entityWrapper.eq("id", parentId);
            Menu menu = menuMapper.selectOne(entityWrapper);
            if(menu != null){
                sysMenu.setParentMenuName(menu.getMenuName());
            }
        }
    }
}
