package com.allenyll.sw.system.service.member.impl;

import com.allenyll.sw.system.mapper.member.CustomerLevelMapper;
import com.allenyll.sw.system.service.member.ICustomerLevelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.common.entity.customer.CustomerLevel;
import org.springframework.stereotype.Service;

/**
 * 会员等级表
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-05-18 16:03:02
 */
@Service("customerLevelService")
public class CustomerLevelServiceImpl extends ServiceImpl<CustomerLevelMapper, CustomerLevel> implements ICustomerLevelService {

}
