package com.allenyll.sw.common.entity.customer;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.allenyll.sw.common.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;


/**
 * 会员余额表
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-04-10 16:16:16
 */
@Data
@TableName("snu_customer_balance")
public class CustomerBalance extends BaseEntity<CustomerBalance> {

	private static final long serialVersionUID = 1L;

	// 用户余额ID
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

	//
    private Long customerId;

	// 余额
    private BigDecimal balance;

	// 提现金额
    private BigDecimal withdrawCash;

}
