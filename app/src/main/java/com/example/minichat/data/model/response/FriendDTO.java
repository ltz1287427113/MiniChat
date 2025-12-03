package com.example.minichat.data.model.response;

public class FriendDTO {
    private  int friendUserid;
    private String friendUsername;
    private String friendRemark;
    private String friendAvatarUrl;

    public int getFriendUserid() {
        return friendUserid;
    }

    public String getFriendUsername() { return friendUsername; }
    public String getFriendRemark() { return friendRemark; }
    public String getFriendAvatarUrl() { return friendAvatarUrl; }
}