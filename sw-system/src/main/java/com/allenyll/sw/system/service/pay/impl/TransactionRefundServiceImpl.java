package com.allenyll.sw.system.service.pay.impl;

import com.allenyll.sw.system.mapper.pay.TransactionRefundMapper;
import com.allenyll.sw.system.service.pay.ITransactionRefundService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.common.entity.pay.TransactionRefund;
import org.springframework.stereotype.Service;

/**
 * 交易退款
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2020-11-23 17:32:11
 */
@Service("transactionRefundService")
public class TransactionRefundServiceImpl extends ServiceImpl<TransactionRefundMapper,TransactionRefund> implements ITransactionRefundService {

}
