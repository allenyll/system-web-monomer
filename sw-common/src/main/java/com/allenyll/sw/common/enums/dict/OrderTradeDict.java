package com.allenyll.sw.common.enums.dict;

import com.allenyll.sw.common.util.StringUtil;

/**
 * @Description:  订单售后字典
 * @Author:       allenyll
 * @Date:         2020/11/19 上午9:55
 * @Version:      1.0
 */
public enum OrderTradeDict {

    /**
     * 未完成
     */
    START("SW1201", "未完成"),
    COMPLETE("SW1202", "已完成"),
    CANCEL("SW1203", "取消"),
    EXCEPTION("SW1204", "异常");

    String code;
    String message;

    OrderTradeDict(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 根据编码获取编码信息
     * @param code
     * @return
     */
    public static String codeToMessage(String code){
        if(StringUtil.isEmpty(code)){
            return "";
        }
        for(OrderTradeDict sexDict : OrderTradeDict.values()){
            if(code.equals(sexDict.getCode())){
                return sexDict.getMessage();
            }
        }
        return "";
    }
}
