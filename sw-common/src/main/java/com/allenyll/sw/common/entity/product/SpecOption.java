package com.allenyll.sw.common.entity.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.allenyll.sw.common.entity.BaseEntity;
import lombok.Data;


/**
 * 规格选项
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-05-13 16:09:07
 */
@Data
@TableName("snu_spec_option")
public class SpecOption extends BaseEntity<SpecOption> {

	private static final long serialVersionUID = 1L;

	// 规格选项
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

	//
    private Long specsId;

	// 名称
    private String name;

	// 编码
    private String code;

}
