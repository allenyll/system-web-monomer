package com.allenyll.sw.system.service.market.impl;

import com.allenyll.sw.common.util.MapUtil;
import com.allenyll.sw.common.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.system.mapper.market.MessageMapper;
import com.allenyll.sw.system.service.market.IMessageService;
import com.allenyll.sw.common.entity.market.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 消息表，包含通知，推送，私信等
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-12-25 18:51:28
 */
@Service("messageService")
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {
    
    @Autowired
    private MessageMapper messageMapper;
    
    @Override
    public List<Message> getMessageList(Map<String, Object> params) {
        String msgType = MapUtil.getString(params, "msgType");
        if (StringUtil.isEmpty(msgType)) {
            msgType = "SW2701";
        }
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("TYPE", msgType);
        wrapper.eq("IS_DELETE", 0);
        List<Message> messageList = messageMapper.selectList(wrapper);
        return messageList;
    }
}
