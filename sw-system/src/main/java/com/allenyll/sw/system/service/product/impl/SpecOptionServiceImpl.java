package com.allenyll.sw.system.service.product.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.system.mapper.product.SpecOptionMapper;
import com.allenyll.sw.system.service.product.ISpecsOptionService;
import com.allenyll.sw.common.dto.SpecsDto;
import com.allenyll.sw.common.entity.product.SpecOption;
import com.allenyll.sw.common.util.Result;
import com.allenyll.sw.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 规格选项
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-05-13 16:09:07
 */
@Service("specOptionService")
public class SpecOptionServiceImpl extends ServiceImpl<SpecOptionMapper, SpecOption> implements com.allenyll.sw.system.service.product.ISpecsOptionService {

    @Autowired
    private SpecOptionMapper specOptionMapper;

    @Override
    public String getMaxCode(String specsId) {
        String code = specOptionMapper.getMaxCode(specsId);
        String wordCode = StringUtil.getCharFromStr(code);
        String numCode = String.valueOf(Integer.parseInt(StringUtil.getNumFromStr(code)) + 1);
        return wordCode + numCode;
    }

    @Override
    public Result<List<SpecOption>> getSpecsOptionBySpecsId(SpecsDto specsDto) {
        QueryWrapper<SpecOption> entityWrapper = new QueryWrapper<>();
        entityWrapper.eq("IS_DELETE", 0);
        entityWrapper.eq("SPECS_ID", specsDto.getSpecsId());
        if (StringUtil.isNotEmpty(specsDto.getSpecsOptionName())) {
            entityWrapper.like("NAME", specsDto.getSpecsOptionName());
        }
        List<SpecOption> specOptions = specOptionMapper.selectList(entityWrapper);
        Result<List<SpecOption>> result = new Result<>();
        result.setData(specOptions);
        return result;
    }
}
