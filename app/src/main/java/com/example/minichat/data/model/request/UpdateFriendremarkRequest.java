package com.example.minichat.data.model.request;

public class UpdateFriendremarkRequest {
    private String username;
    private String newRemark;

    public UpdateFriendremarkRequest(String username, String newRemark) {
        this.username = username;
        this.newRemark = newRemark;
    }
}
