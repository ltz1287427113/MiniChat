package com.example.minichat.data.model.request;

public class HandleFriendApplicationRequest {
    private int applicationId;
    private String status;
    private String remark;

    public HandleFriendApplicationRequest(int applicationId, String status, String remark) {
        this.applicationId = applicationId;
        this.status = status;
        this.remark = remark;
    }
}
