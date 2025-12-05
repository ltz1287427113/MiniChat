package com.example.minichat.data.model.response;

/**
 * 扫码响应
 */
public class ScanResponse {
    private String username;    // 目标用户名
    private String nickname;    // 目标昵称
    private String avatarUrl;   // 目标头像

    // Getters
    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}