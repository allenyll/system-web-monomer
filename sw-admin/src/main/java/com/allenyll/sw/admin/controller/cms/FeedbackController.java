package com.allenyll.sw.admin.controller.cms;

import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.common.annotation.Log;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.system.service.cms.impl.FeedbackServiceImpl;
import com.allenyll.sw.common.entity.cms.Feedback;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Description:  意见反馈
 * @Author:       allenyll
 * @Date:         2020/11/4 9:50 上午
 * @Version:      1.0
 */
@Slf4j
@Api(value = "意见反馈", tags = "意见反馈管理")
@RestController
@RequestMapping("/feedback")
public class FeedbackController extends BaseController<FeedbackServiceImpl,Feedback> {

    @ApiOperation("[小程序接口]保存意见")
    @ResponseBody
    @RequestMapping(value = "/saveFeedback", method = RequestMethod.POST)
    public Result saveFeedback(@CurrentUser(isFull = true) User user, @RequestBody Map<String, Object> params){
        service.saveFeedBack(user, params);
        return new Result();
    }

}
