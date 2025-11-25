package com.example.minichat.data.model.request;

public class AddFriendRequest {
    private String username; // 目标用户名 (二选一)
    private String email;    // 目标邮箱 (二选一)
    private String message;  // 申请理由 (如: "我是xxx")
    private String remark;   // 备注名

    // 构造函数
    public AddFriendRequest(String username, String email, String message, String remark) {
        this.username = username;
        this.email = email;
        this.message = message;
        this.remark = remark;
    }
}