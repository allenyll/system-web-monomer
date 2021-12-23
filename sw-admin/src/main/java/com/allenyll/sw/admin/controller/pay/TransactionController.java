package com.allenyll.sw.admin.controller.pay;

import com.allenyll.sw.common.entity.pay.Transaction;
import com.allenyll.sw.system.BaseController;
import com.allenyll.sw.system.service.pay.impl.TransactionServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("transaction")
public class TransactionController extends BaseController<TransactionServiceImpl, Transaction> {


}
