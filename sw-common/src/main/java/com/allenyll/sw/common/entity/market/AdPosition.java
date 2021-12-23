package com.allenyll.sw.common.entity.market;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.allenyll.sw.common.entity.BaseEntity;
import lombok.Data;


/**
 * 广告位表
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-12-19 20:12:58
 */
@Data
@TableName("snu_ad_position")
public class AdPosition extends BaseEntity<AdPosition> {

	private static final long serialVersionUID = 1L;

	// 主键ID
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

	// 名称
    private String name;

	// 长度
    private Integer width;

	// 宽度
    private Integer height;

	// 描述
    private String description;

}
