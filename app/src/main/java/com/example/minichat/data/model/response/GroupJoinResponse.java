package com.example.minichat.data.model.response;

public class GroupJoinResponse {
    private boolean success;
    private String message;
    private int groupId;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getGroupId() {
        return groupId;
    }
}
