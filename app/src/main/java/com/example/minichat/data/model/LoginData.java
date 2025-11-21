package com.example.minichat.data.model;

public class LoginData {
    private String token;
    private UserLoginResponse userLoginResponse;

    public String getToken() {
        return token;
    }

    public UserLoginResponse getUserLoginResponse() {
        return userLoginResponse;
    }
}