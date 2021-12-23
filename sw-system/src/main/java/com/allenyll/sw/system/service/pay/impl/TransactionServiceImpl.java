package com.allenyll.sw.system.service.pay.impl;

import com.allenyll.sw.system.mapper.pay.TransactionMapper;
import com.allenyll.sw.system.service.pay.ITransactionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.common.entity.pay.Transaction;
import org.springframework.stereotype.Service;

/**
 * 交易表
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-04-04 16:37:41
 */
@Service("transactionService")
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, Transaction> implements ITransactionService {

}
