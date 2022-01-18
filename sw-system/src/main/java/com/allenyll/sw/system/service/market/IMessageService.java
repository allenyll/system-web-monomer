package com.allenyll.sw.system.service.market;

import com.baomidou.mybatisplus.extension.service.IService;
import com.allenyll.sw.common.entity.market.Message;

import java.util.List;
import java.util.Map;

public interface IMessageService extends IService<Message> {

    /**
     * 获取消息集合
     * @param params
     * @return
     */
    List<Message> getMessageList(Map<String, Object> params);
}
