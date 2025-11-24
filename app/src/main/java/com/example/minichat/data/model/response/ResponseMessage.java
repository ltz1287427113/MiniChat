package com.example.minichat.data.model.response;

/**
 * 根据接口文档定义的通用响应结构
 * 对应 schema: ResponseMessage
 */
public class ResponseMessage<T> {

    // 对应文档: status (200-成功；400-客户端错误...)
    private int status;

    // 对应文档: message (响应描述信息)
    private String message;

    // 对应文档: data (业务数据，成功时返回具体数据，失败时可为null)
    private T data;

    // --- Getters ---
    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    // 辅助方法：判断是否成功 (文档说 200 为成功)
    public boolean isSuccess() {
        return status == 200;
    }
}