package com.allenyll.sw.system.service.market.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.allenyll.sw.system.mapper.market.MessageMapper;
import com.allenyll.sw.system.service.market.IMessageService;
import com.allenyll.sw.common.entity.market.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 消息表，包含通知，推送，私信等
 *
 * @author allenyll
 * @email 806141743@qq.com
 * @date 2019-12-25 18:51:28
 */
@Service("messageService")
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageServiceImpl.class);
}
