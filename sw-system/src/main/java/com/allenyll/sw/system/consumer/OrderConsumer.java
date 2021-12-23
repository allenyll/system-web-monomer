package com.allenyll.sw.system.consumer;

import com.allenyll.sw.common.util.MapUtil;
import com.allenyll.sw.system.service.order.IOrderService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Component
public class OrderConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);

    @Autowired
    IOrderService orderService;

    @RabbitListener(queues = "sw.order.cancel")
    public void handle(HashMap<String, Object> map, Channel channel, 
                       CorrelationData correlationData, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        LOGGER.info("receive delay message orderId:{}", MapUtil.getString(map, "orderId"));
        map.put("note", "支付超时自动取消");
        map.put("opt_name", "rabbit");
        // channel.basicAck(tag, false); //tag: 消息tag
        orderService.cancelOrder(map);
    }
}
