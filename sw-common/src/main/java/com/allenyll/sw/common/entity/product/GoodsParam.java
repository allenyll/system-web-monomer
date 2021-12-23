package com.allenyll.sw.common.entity.product;

import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * @Description:  商品参数
 * @Author:       allenyll
 * @Date:         2019-05-28 16:05
 * @Version:      1.0
 */
@Data
@ToString
public class GoodsParam extends Goods {

    private String categoryName;

    private Brand brand;

    private Long parentCategoryId;

    private Long parentSpecCategoryId;

    private List<GoodsFullReduce> goodsFullReduceList;

    private List<GoodsLadder> goodsLadderList;

    private List<Sku> skuStockList;

    private List<Map<String, Object>> specsList;

    private List<Map<String, Object>> skuStockMapList;

    private List<Map<String, String>> selectSkuPics;

    /**
     * 颜色
     */
    private String color;

    /**
     * 尺码
     */
    private String size;
}
