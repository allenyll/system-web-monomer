package com.allenyll.sw.admin.controller.product;

import com.allenyll.sw.system.service.product.impl.GoodsAppraisesServiceImpl;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.common.entity.product.GoodsAppraises;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("oodsAppraises")
public class GoodsAppraisesController extends BaseController<GoodsAppraisesServiceImpl, GoodsAppraises> {


}
