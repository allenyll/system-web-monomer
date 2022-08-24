package com.allenyll.sw.common.enums.dict;

import com.allenyll.sw.common.util.StringUtil;

/**
 * 积分类型字典
 * @author yuleilei
 */
public enum PointDict {

    // 无积分
    NO("SW0500", "无积分"),
    // 收入积分
    IN("SW0501", "收入"),
    // 支出积分
    OUT("SW0502", "支出");

    String code;
    String message;

    PointDict(String code, String message){
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
        for(PonitDict sexDict : PonitDict.values()){
            if(code.equals(sexDict.getCode())){
                return sexDict.getMessage();
            }
        }
        return "";
    }
}
