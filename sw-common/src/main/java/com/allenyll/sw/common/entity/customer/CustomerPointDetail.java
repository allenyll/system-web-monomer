package com.allenyll.sw.common.entity.customer;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.allenyll.sw.common.entity.BaseEntity;
import lombok.Data;
import lombok.ToString;

/**
 * <p>
 *
 * </p>
 *
 * @author yu.leilei
 * @since 2019-01-09
 */
@Data
@ToString
@TableName("snu_customer_point_detail")
public class CustomerPointDetail extends BaseEntity<CustomerPointDetail> {

    private static final long serialVersionUID = 1L;

    /**
     * 积分明细表
     */
    @TableId(type = IdType.ASSIGN_ID)
	private Long id;
    /**
     * 用户id
     */
	private Long customerId;

	/**
	 * 积分数值
	 */
	private Integer point;

	/**
	 * 积分类型
	 */
	private String type;

	/**
	 * 过期时间
	 */
	private String expiredTime;

	/**
	 * 获得时间
	 */
	private String getTime;

	/**
	 * 备注
	 */
	private String remark;

}
