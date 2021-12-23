package com.allenyll.sw.common.entity.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.allenyll.sw.common.entity.BaseEntity;
import lombok.Data;


/**
 * 规格组
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-05-13 16:05:43
 */
@Data
@TableName("snu_specs_group")
public class SpecsGroup extends BaseEntity<SpecsGroup> {

	private static final long serialVersionUID = 1L;

	// 规格主键
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

	// 名称
    private String name;

	// 编码
    private String code;

}
