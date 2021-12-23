package com.allenyll.sw.common.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 交易退款
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2020-11-23 17:32:11
 */
@ToString
@Data
public class TransactionRefundDto implements Serializable{

	private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    private Long id;

    /**
    * 退款单号
    */
    private String refundNo;

    /**
    * 申请单ID
    */
    private Long aftersaleId;

    /**
    * 关联订单
    */
    private Long orderId;

    /**
    * 订单明细ID
    */
    private Long orderDetailId;

    /**
    * 退款人
    */
    private Long customerId;

    /**
    * 退款金额
    */
    private BigDecimal amount;

    /**
    * 退还积分
    */
    private Integer integral;

    /**
    * 退还方式
    */
    private String returnType;

    /**
    * 退款状态 SW1201 未完成  SW1202 已完成 SW1203 取消 SW1204 异常
    */
    private String status;

    /**
    * 退款时间
    */
    private String refundTime;

    /**
    * 备注
    */
    private String remark;

}
