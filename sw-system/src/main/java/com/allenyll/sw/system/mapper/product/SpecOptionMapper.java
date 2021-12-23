package com.allenyll.sw.system.mapper.product;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.allenyll.sw.common.entity.product.SpecOption;

/**
 * 规格选项
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-05-13 16:09:07
 */
public interface SpecOptionMapper extends BaseMapper<SpecOption> {

    /**
     * 获取最大编码
     * @param specsId
     * @return
     */
    String getMaxCode(String specsId);
}
