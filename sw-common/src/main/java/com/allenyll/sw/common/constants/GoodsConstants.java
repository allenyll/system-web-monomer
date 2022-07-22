package com.allenyll.sw.common.constants;

/**
 * @Description:  订单相关常量
 * @Author:       allenyll
 * @Date:         2022/2/22 4:49 下午
 * @Version:      1.0
 */
public class GoodsConstants {

    public interface Goods {
        
        String GOODS_SORT_DEFAULT = "default";
        
        String GOODS_SORT_CATEGORY = "category";
        
        String GOODS_SORT_ASC = "asc";
        
    }

    public interface GoodsLabel {
        String GOODS_LABEL_USED = "isUsed";
        
        String GOODS_LABEL_RECOM = "isRecom";
        
        String GOODS_LABEL_SPEC = "isSpec";
        
        String GOODS_LABEL_BEST = "isBest";
        
        String GOODS_LABEL_HOT = "isHot";
        
        String GOODS_LABEL_NEW = "isNew";
    }

    public interface GoodsType {
        String GOODS_LABEL_RECOM = "recommend";

        String GOODS_LABEL_BEST = "best";

        String GOODS_LABEL_HOT = "hot";

        String GOODS_LABEL_NEW = "new";
    }


}
