package com.allenyll.sw.common.dto;


import com.allenyll.sw.common.entity.product.GoodsParam;
import lombok.Data;

import java.util.List;

/**
 * 商品相关查询条件参数DTO
 * @ClassName: com.allenyll.sw.common.dto.GoodsQueryDto.java
 * @Description:
 * @author: 20012055 yuleilei
 * @date:  2020/12/18 14:18
 * @version V1.0
 */
@Data
public class GoodsQueryDto extends BaseQueryDto {

    /**
     * 排序类型
     */
    private String sort;

    /**
     * 排序方式
     */
    private String order;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 商品分类ID
     */
    private Long categoryId;

    /**
     * 品牌
     */
    private Long brandId;

    /**
     * 商品状态
     */
    private String status;

    /**
     * 启用状态
     */
    private String isUsed;

    /**
     * 年份
     */
    private String year;

    /**
     * 单位
     */
    private String unit;

    /**
     * 季节
     */
    private String season;

    /**
     * 商品集合
     */
    private List<GoodsParam> goodsList;

}
