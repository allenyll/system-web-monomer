package com.allenyll.sw.system.service.product;

import com.allenyll.sw.common.util.DataResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.allenyll.sw.common.dto.GoodsQueryDto;
import com.allenyll.sw.common.dto.GoodsResult;
import com.allenyll.sw.common.entity.product.Goods;
import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.Result;

import java.util.List;
import java.util.Map;

public interface IGoodsService extends IService<Goods> {

    /**
     * 设置商品相关属性
     * @param goods
     */
    void setFile(Goods goods);

    /**
     * 获取商品列表
     * @param goodsQueryDto
     * @return
     */
    Result<GoodsResult> getGoodsListByCondition(GoodsQueryDto goodsQueryDto);

    /**
     * 获取商品库存信息
     * @param goodsQueryDto
     * @return
     */
    Result<GoodsResult> getStock(GoodsQueryDto goodsQueryDto);

    /**
     * 删除商品
     * @param user
     * @return
     */
    int deleteGoods(User user, Long id);

    /**
     * 导入商品
     * @param goodsQueryDto
     * @return
     */
    Result<GoodsResult> importGoods(GoodsQueryDto goodsQueryDto, User user);

    /**
     * 获取商品列表
     * @param params
     * @return
     */
    DataResponse getGoodsList(Map<String, Object> params);

    /**
     * 根据商品类型获取商品列表
     * @param params
     * @return
     */
    List<Goods> getGoodsListByType(Map<String, Object> params);

    /**
     * 更新商品状态
     * @param user
     * @param params
     * @return
     */
    DataResponse updateLabel(User user, Map<String, Object> params);

    /**
     * 根据分类获取商品
     * @param params
     * @return
     */
    DataResponse getGoodsByCategory(Map<String, Object> params);
}
