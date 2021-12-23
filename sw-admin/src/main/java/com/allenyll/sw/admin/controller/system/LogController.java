package com.allenyll.sw.admin.controller.system;


import com.allenyll.sw.common.entity.system.Log;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.DataResponse;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.system.base.impl.LogServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 * 记录日志 前端控制器
 * </p>
 *
 * @author yu.leilei
 * @since 2018-12-23
 */
@Api(value = "日志相关接口", tags = "日志管理")
@Controller
@RequestMapping("log")
public class LogController extends BaseController<LogServiceImpl, Log> {

    @ApiOperation(value = "日志记录")
    @ResponseBody
    @PostMapping("saveLog")
    public DataResponse saveLog(@RequestBody Log log) {
        User user = new User();
        user.setId(0L);
        return super.add(user, log);
    }

}
