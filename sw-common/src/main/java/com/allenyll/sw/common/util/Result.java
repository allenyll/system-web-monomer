package com.allenyll.sw.common.util;

/**
 * @Description:  通用返回结果
 * @Author:       allenyll
 * @Date:         2020/6/1 7:07 下午
 * @Version:      1.0
 */
public class Result<T> extends BaseResult{

    private static final long serialVersionUID = 1L;

    private T data;

    public Result(){
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
