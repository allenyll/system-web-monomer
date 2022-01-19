package com.allenyll.sw.admin.controller.market;

import com.allenyll.sw.common.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.allenyll.sw.system.service.market.impl.MessageServiceImpl;
import com.allenyll.sw.common.annotation.CurrentUser;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.common.entity.market.Message;
import com.allenyll.sw.common.entity.system.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Api("消息接口")
@Controller
@RequestMapping("message")
public class MessageController extends BaseController<MessageServiceImpl, Message> {

    @Override
    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public DataResponse add(@CurrentUser(isFull = true) User user, @RequestBody Message entity) {
        entity.setId(SnowflakeIdWorker.generateId());
        return super.add(user, entity);
    }

    @ApiOperation("[小程序接口]根据类型获取消息")
    @ResponseBody
    @RequestMapping(value = "getMessageListByType", method = RequestMethod.POST)
    public Result getMessageListByType(@RequestBody Map<String, Object> params) {
        Result result = new Result();
        Map<String, Object> data = new HashMap<>();
        List<Message> list = service.getMessageList(params);
        data.put("messageList", list);
        result.setData(data);
        return result;
    }

    @ApiOperation("[小程序接口]根据ID获取消息")
    @ResponseBody
    @RequestMapping(value = "getMessageById", method = RequestMethod.POST)
    public Result getMessageById(@RequestBody Map<String, Object> params){
        Result result = new Result();
        Map<String, Object> data = new HashMap<>();
        Long id = MapUtil.getLong(params, "id");
        if (id == null) {
            log.error("缺少必要参数！");
            result.fail("系统异常，请联系管理员");
            return result;
        }
        Message message = service.getById(id);
        data.put("message", message);
        result.setData(data);
        return result;
    }
}
