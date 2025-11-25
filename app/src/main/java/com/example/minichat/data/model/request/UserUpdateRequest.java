package com.example.minichat.data.model.request;

public class UserUpdateRequest {
    private String nickname;
    private String avatarUrl;

    public UserUpdateRequest(String nickname, String avatarUrl) {
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
    }
}
