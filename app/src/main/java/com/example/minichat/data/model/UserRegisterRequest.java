package com.example.minichat.data.model;

public class UserRegisterRequest {
    private String username;
    private String email;
    private String password;
    private String code; // 验证码

    public UserRegisterRequest(String username, String email, String password, String code) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.code = code;
    }
}