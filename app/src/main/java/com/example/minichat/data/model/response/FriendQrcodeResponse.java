package com.example.minichat.data.model.response;

/**
 * 好友二维码响应
 */
public class FriendQrcodeResponse {
    private int userId;
    private String qrcodeUrl;
    private long expireSeconds;

    // Getters
    public int getUserId() {
        return userId;
    }

    public String getQrcodeUrl() {
        return qrcodeUrl;
    }

    public long getExpireSeconds() {
        return expireSeconds;
    }

    // Setters (for testing)
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl;
    }

    public void setExpireSeconds(long expireSeconds) {
        this.expireSeconds = expireSeconds;
    }
}