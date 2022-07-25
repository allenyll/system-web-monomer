package com.allenyll.sw.admin.controller.order;

import com.allenyll.sw.common.entity.order.OrderOperateLog;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.system.service.order.impl.OrderOperateLogServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Api(tags = "订单操作记录管理")
@Controller
@RequestMapping("/orderOperateLog")
public class OrderOperateLogController extends BaseController<OrderOperateLogServiceImpl, OrderOperateLog> {


}
