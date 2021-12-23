package com.allenyll.sw.admin.controller.pay;

import com.allenyll.sw.common.entity.pay.TransactionRefund;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.system.service.pay.impl.TransactionRefundServiceImpl;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 交易退款
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2020-11-23 17:32:11
 */
@Slf4j
@Api(value = "交易退款", tags = "交易退款")
@RestController
@RequestMapping("/transactionRefund")
public class TransactionRefundController extends BaseController<TransactionRefundServiceImpl,TransactionRefund> {


}
