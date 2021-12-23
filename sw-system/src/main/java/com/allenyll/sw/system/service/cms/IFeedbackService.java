package com.allenyll.sw.system.service.cms;

import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import com.allenyll.sw.common.entity.cms.Feedback;

import java.util.Map;

public interface IFeedbackService extends IService<Feedback> {

    /**
     * 微信小程序建议--新增
     * @param user
     * @param params
     * @return
     */
    void saveFeedBack(User user, Map<String, Object> params);
}
