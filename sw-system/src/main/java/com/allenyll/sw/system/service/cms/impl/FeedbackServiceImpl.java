package com.allenyll.sw.system.service.cms.impl;

import com.allenyll.sw.common.entity.system.User;
import com.allenyll.sw.common.util.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.system.mapper.cms.FeedbackMapper;
import com.allenyll.sw.system.service.cms.IFeedbackService;
import com.allenyll.sw.common.entity.cms.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 意见反馈表
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2020-11-03 14:44:18
 */
@Service("feedbackService")
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper,Feedback> implements IFeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;
    
    @Override
    public void saveFeedBack(User user, Map<String, Object> params) {
        String type = MapUtil.getString(params, "type");
        String content = MapUtil.getString(params, "content");
        String phone = MapUtil.getString(params, "phone");
        if (StringUtil.isEmpty(phone) || StringUtil.isEmpty(content) || StringUtil.isEmpty(type)) {
            return;
        }
        Feedback feedback = new Feedback();
        feedback.setId(SnowflakeIdWorker.generateId());
        feedback.setType(type);
        feedback.setContent(content);
        feedback.setPhone(phone);
        feedback.setIsDelete(0);
        feedback.setAddTime(DateUtil.getCurrentDateTime());
        feedback.setUpdateTime(DateUtil.getCurrentDateTime());
        feedback.setAddUser(user.getId());
        feedback.setUpdateUser(user.getId());
        feedbackMapper.insert(feedback);
    }
}
