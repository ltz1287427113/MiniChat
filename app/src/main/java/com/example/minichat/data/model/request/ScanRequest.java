package com.example.minichat.data.model.request;

/**
 * 扫码请求
 */
public class ScanRequest {
    private String content;        // 二维码内容
    private int currentUserId;     // 当前用户ID
    private String message;        // 验证消息
    private String remark;         // 备注

    public ScanRequest(String content, int currentUserId, String message, String remark) {
        this.content = content;
        this.currentUserId = currentUserId;
        this.message = message;
        this.remark = remark;
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}