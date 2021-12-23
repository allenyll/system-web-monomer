package com.allenyll.sw.admin.controller.system;


import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.system.BaseController;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.allenyll.sw.common.constants.BaseConstants;
import com.allenyll.sw.common.entity.system.Menu;
import com.allenyll.sw.common.entity.system.MenuTree;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.common.util.SnowflakeIdWorker;
import com.allenyll.sw.system.base.impl.MenuServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 菜单管理 前端控制器
 * </p>
 *
 * @author yu.leilei
 * @since 2018-06-12
 */
@Api(value = "菜单相关接口", tags = "菜单管理")
@Controller
@RequestMapping("menu")
public class MenuController extends BaseController<MenuServiceImpl, Menu> {

    private static final Logger LOG = LoggerFactory.getLogger(MenuController.class);

    @Override
    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public DataResponse add(@CurrentUser(isFull = true) User user, @RequestBody Menu menu) {
        menu.setId(SnowflakeIdWorker.generateId());
        return super.add(user, menu);
    }

    /**
     * 获取菜单
     * @param params
     * @return
     */
    @ApiOperation(value = "获取全部的菜单信息")
    @ResponseBody
    @RequestMapping(value = "/getAllMenu", method = RequestMethod.GET)
    public DataResponse getAllMenu(@RequestParam Map<String, Object> params){
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<Menu> wrapper = super.mapToWrapper(params);
        List<Menu> menuList = service.list(wrapper);
        List<MenuTree> list = service.getMenuTree(menuList, BaseConstants.MENU_ROOT);
        result.put("menus", list);
        return DataResponse.success(result);
    }

    @ApiOperation(value = "根据菜单ID获取菜单")
    @Override
    @ResponseBody
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public DataResponse get(@PathVariable Long id){
       return service.getMenuById(id);
    }

    @ApiOperation(value = "组装菜单树")
    @ResponseBody
    @RequestMapping(value = "getMenuTree", method = RequestMethod.GET)
    public DataResponse getMenuTree(String type){
        return service.getMenuTreeList(type);
    }
}
