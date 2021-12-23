package com.allenyll.sw.system.service.product;

import com.baomidou.mybatisplus.extension.service.IService;
import com.allenyll.sw.common.dto.SpecsDto;
import com.allenyll.sw.common.entity.product.SpecOption;
import com.allenyll.sw.common.util.Result;

import java.util.List;

public interface ISpecsOptionService extends IService<SpecOption> {

    /**
     * 获取最大编码
     * @param specsId
     * @return
     */
    String getMaxCode(String specsId);

    /**
     * 根据规格获取规格选项列表
     * @param specsDto
     * @return
     */
    Result<List<SpecOption>> getSpecsOptionBySpecsId(SpecsDto specsDto);
}
