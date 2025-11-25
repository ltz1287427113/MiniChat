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

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public String getStatus() {
        return status;
    }

    public String getRemark() {
        return remark;
    }
}
