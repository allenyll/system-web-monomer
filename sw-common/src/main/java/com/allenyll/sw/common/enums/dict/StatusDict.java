package com.allenyll.sw.common.enums.dict;

import com.allenyll.sw.common.util.StringUtil;

/**
 * @Description:  日志类型
 * @Author:       allenyll
 * @Date:         2018/12/24 9:45 AM
 * @Version:      1.0
 */
public enum StatusDict {

    /**
     * 停用状态
     */
    STOP("SW1301", "停用"),
    /**
     * 启用状态
     */
    START("SW1302", "启用");

    String code;
    String message;

    StatusDict(String code, String message){
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
        for(StatusDict LogType : StatusDict.values()){
            if(code.equals(LogType.getCode())){
                return LogType.getMessage();
            }
        }
        return "";
    }
}
