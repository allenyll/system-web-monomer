package com.allenyll.sw.common.entity.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.allenyll.sw.common.entity.BaseEntity;
import lombok.Data;

/**
 * 商品品牌
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-03-21 10:04:09
 */
@Data
@TableName("snu_brand")
public class Brand extends BaseEntity<Brand> {

	private static final long serialVersionUID = 1L;

	// 品牌主键
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

	//
    private String brandName;

	// 品牌编码
    private String brandNo;

	// 品牌类型
    private String brandType;

}
