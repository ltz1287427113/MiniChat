package com.example.minichat.data.model.response;

public class ApplicationResponse {
    private int applicationId;
    private String nickname;
    private String remark;
    private String message; // 申请理由
    private String username; // 微信号
    private String avatarUrl;
    private String status; // "PENDING", "ACCEPTED", "REJECTED"
    private String createdAt;

    // Getters
    public int getApplicationId() { return applicationId; }
    public String getNickname() { return nickname; }
    public String getRemark() { return remark; }
    public String getMessage() { return message; }
    public String getUsername() { return username; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
}