package com.allenyll.sw.admin.controller.order;

import com.allenyll.sw.common.entity.order.OrderOperateLog;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.system.service.order.impl.OrderOperateLogServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orderOperateLog")
public class OrderOperateLogController extends BaseController<OrderOperateLogServiceImpl, OrderOperateLog> {


}
