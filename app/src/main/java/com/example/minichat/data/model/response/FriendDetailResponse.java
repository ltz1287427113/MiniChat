package com.example.minichat.data.model.response;

public class FriendDetailResponse {
    private String nickname;
    private String username;     // 好友微信号
    private String friendRemark; // 备注
    private String avatarUrl;
    private String email;

    // Getters
    public String getNickname() { return nickname; }
    public String getUsername() { return username; }
    public String getFriendRemark() { return friendRemark; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getEmail() { return email; }

    // 辅助方法：获取显示用的名字（有备注优先显示备注）
    public String getDisplayName() {
        return (friendRemark != null && !friendRemark.isEmpty()) ? friendRemark : nickname;
    }
}