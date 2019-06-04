package com.example.rxjava_retrofit;

/**
 * @创建者 mingyan.su
 * @创建时间 2019/5/31 18:16
 * @类描述 ${TODO}回调数据统一封装类
 */
public class Data<T> {
    private int status;
    private String msg;
    private T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
