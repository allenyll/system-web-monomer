package com.allenyll.sw.common.constants;

/**
 * @Description:  订单相关常量
 * @Author:       allenyll
 * @Date:         2022/2/22 4:49 下午
 * @Version:      1.0
 */
public class OrderConstants {

    public interface Order {
        String ORDER_CANCEL_TYPE = "rabbit";
        
        String ORDER_UPDATE_TYPE = "order";
    }
    
    public interface OrderLog {
        
        String ORDER_LOG_CLOSE = "close";
        
        String ORDER_LOG_DELETE = "delete";
        
        String ORDER_LOG_DELIVERY = "delivery";
        
        String ORDER_LOG_MONEY = "money";
        
        String ORDER_LOG_RECEIVER = "receiver";
        
        String ORDER_LOG_NOTE= "note";
        
        String ORDER_LOG_CANCEL = "cancel";
    }

}
