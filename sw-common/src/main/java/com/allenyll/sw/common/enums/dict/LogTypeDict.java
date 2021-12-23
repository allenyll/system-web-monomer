package com.allenyll.sw.common.enums.dict;

import com.allenyll.sw.common.util.StringUtil;

/**
 * @Description:  日志类型
 * @Author:       allenyll
 * @Date:         2018/12/24 9:45 AM
 * @Version:      1.0
 */
public enum LogTypeDict {

    LOGIN("SW0301", "系统日志"),
    OPERATE("SW0302", "操作日志");

    String code;
    String message;

    LogTypeDict(String code, String message){
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
        if (StringUtil.isEmpty(code)) {
            return "";
        }
        for(LogTypeDict LogType : LogTypeDict.values()){
            if(code.equals(LogType.getCode())){
                return LogType.getMessage();
            }
        }
        return "";
    }
}
