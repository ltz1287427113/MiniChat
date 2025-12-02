package com.example.minichat.data.model.request;

public class UserUpdateRequest {
    private String nickname;

    public UserUpdateRequest(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
