package com.example.minichat.data.model.response;

/**
 * 扫码响应
 */
public class ScanResponse {
    private StrangerResponse strangerResponse;
    private GroupJoinResponse groupJoinResponse;

    public StrangerResponse getStrangerResponse() {
        return strangerResponse;
    }

    public GroupJoinResponse getGroupJoinResponse() {
        return groupJoinResponse;
    }
}