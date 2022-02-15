package com.allenyll.sw.sso.controller;

import com.alibaba.fastjson.JSON;
import com.allenyll.sw.common.dto.CustomerQueryDto;
import com.allenyll.sw.common.entity.customer.Customer;
import com.allenyll.sw.common.util.Result;
import com.allenyll.sw.common.util.StringUtil;
import com.allenyll.sw.common.entity.auth.AuthToken;
import com.allenyll.sw.sso.service.impl.WxUserServiceImpl;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Description:
 * @Author:       allenyll
 * @Date:         2020/8/20 2:22 下午
 * @Version:      1.0
 */
@RestController
@RequestMapping("/wx/auth")
public class WxAuthController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WxAuthController.class);

    @Autowired
    private WxUserServiceImpl wxUserService;
    /**
     * 微信小程序登录
     * @return
     */
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public Result<AuthToken> token(HttpServletRequest request, @RequestBody CustomerQueryDto customerQueryDto) {
        Result<AuthToken> result = new Result<>();
        String code = customerQueryDto.getCode();
        String wxMode = customerQueryDto.getMode();
        Customer customer = customerQueryDto.getCustomer();
        LOGGER.info("用户信息：{}", JSON.toJSONString(customer));
        //校验参数
        if (StringUtil.isEmpty(code)) {
            LOGGER.error("小程序认证编码code不能为空");
            result.fail("小程序认证编码code不能为空");
            return result;
        }

        AuthToken authToken = wxUserService.token(request, code, wxMode, customer);
        
        if (authToken == null) {
            result.fail("小程序获取登录失败");
            return result;
        }

        result.setData(authToken);
        return result;
    }

}
