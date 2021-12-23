package com.allenyll.sw.admin.controller.system;


import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.common.annotation.Log;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.common.entity.system.Depot;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.common.util.SnowflakeIdWorker;
import com.allenyll.sw.system.base.impl.DepotServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 管理部门 前端控制器
 * </p>
 *
 * @author yu.leilei
 * @since 2018-11-20
 */
@Api(value = "组织相关接口", tags = "组织管理")
@Controller
@RequestMapping("/depot/")
public class DepotController extends BaseController<DepotServiceImpl, Depot> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepotController.class);

    @Override
    @ResponseBody   
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @Log("添加部门")
    public DataResponse add(@CurrentUser(isFull = true) User user, @RequestBody Depot depot) {
        depot.setId(SnowflakeIdWorker.generateId());
        return super.add(user, depot);
    }

    @ApiOperation(value = "部门列表")
    @ResponseBody
    @RequestMapping(value = "getAllDepot", method = RequestMethod.GET)
    public DataResponse getDepotList(String name){
        return service.getDepotList(name);
    }

    @ApiOperation(value = "获取所有部门--树形结构")
    @ResponseBody
    @RequestMapping(value = "getDepotTree", method = RequestMethod.GET)
    public DataResponse getDepotTree(){
        return service.getDepotTree();
    }

    @ApiOperation(value = "根据部门ID获取具体部门")
    @Override
    @ResponseBody
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public DataResponse get(@PathVariable Long id){
        return service.getDepot(id);
    }

}
