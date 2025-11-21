package com.example.minichat.data.model;

/**
 * 对应文档: UserLoginRequest
 */
public class UserLoginRequest {
    private String username; // 用户登录名（通常为邮箱）
    private String password; // 用户登录密码

    public UserLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}