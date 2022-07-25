package com.allenyll.sw.admin.controller.member;


import com.allenyll.sw.common.entity.customer.CustomerPointDetail;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.system.service.member.impl.CustomerPointDetailServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yu.leilei
 * @since 2019-01-09
 */
@Api(tags = "会员积分明细管理")
@RestController
@RequestMapping("customerPointDetail")
public class CustomerPointDetailController extends BaseController<CustomerPointDetailServiceImpl, CustomerPointDetail> {

}
