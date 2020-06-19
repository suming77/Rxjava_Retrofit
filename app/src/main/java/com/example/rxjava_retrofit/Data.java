package com.example.rxjava_retrofit;

/**
 * @创建者 mingyan.su
 * @创建时间 2019/5/31 18:16
 * @类描述 ${TODO}步骤三：回调数据统一封装类
 */
public class Data<T> {
    private int code;
    private String message;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
