package com.example.minichat.data.model.response;

public class JwtResponse {
    private String token;
    private UserLoginResponse userLoginResponse;

    public String getToken() {
        return token;
    }

    public UserLoginResponse getUserLoginResponse() {
        return userLoginResponse;
    }
}