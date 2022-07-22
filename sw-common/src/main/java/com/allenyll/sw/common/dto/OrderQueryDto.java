package com.allenyll.sw.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description:  订单查询条件
 * @Author:       allenyll
 * @Date:         2020/11/11 11:09 上午
 * @Version:      1.0
 */
@Data
public class OrderQueryDto extends BaseQueryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 查询同一接口来源类型
     */
    private String type;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单明细ID
     */
    private Long orderDetailId;

    /**
     * 售后申请单号
     */
    private String aftersaleNo;

    /**
     * 用户
     */
    private String customerName;

    /**
     * 售后状态
     */
    private String aftersaleStatus;

    /**
     * 售后申请时间开始
     */
    private String applyTimeStart;

    /**
     * 售后申请时间结束
     */
    private String applyTimeEnd;

    /**
     * 售后数量
     */
    private Integer quantity;
    
    private String attributes;
    
    private String goodsName;
    
    private BigDecimal goodsPrice;
    
    private String pic;
}
