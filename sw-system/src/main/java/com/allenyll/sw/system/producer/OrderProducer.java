package com.allenyll.sw.system.producer;

import com.allenyll.sw.common.util.MapUtil;
import com.allenyll.sw.core.mq.QueueEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderProducer implements RabbitTemplate.ConfirmCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderProducer.class);

    @Autowired
    private AmqpTemplate amqpTemplate;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    public OrderProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setConfirmCallback(this);
    }

    public void sendMessage(Map<String, Object> param, long delayTimes){
        LOGGER.info("send delay message orderId:{}", MapUtil.getString(param, "orderId"));
        CorrelationData correlationData = new CorrelationData(MapUtil.getString(param, "orderId"));
        rabbitTemplate.convertAndSend(QueueEnum.QUEUE_ORDER_CANCEL_TTL.getExchange(), 
                QueueEnum.QUEUE_ORDER_CANCEL_TTL.getRouteKey(), param, message -> {
            message.getMessageProperties().setExpiration(String.valueOf(delayTimes));
            return message;
        }, correlationData);
        // TODO 将消息冗余到消息备份表，防止投送失败造成订单丢失
        
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String message) {
        LOGGER.info("消息回调ID: " + correlationData);
        if (!ack) {
            LOGGER.info("订单取消失败：" + message);
        } else {
            LOGGER.info("消息投送成功");
            // TODO 将订单消息表消息状态更新为已成功投送
        }
    }
}
