package com.example.minichat.model;

/**
 * [升级]
 * 现在的 ContactItem 包含了头像 URL，
 * 并且字段名改为了 username 和 displayName，更符合业务。
 */
public class ContactItem {

    private String username;    // 对应后端的 friendUsername
    private String displayName; // 对应后端的 friendRemark
    private String avatarUrl;   // 对应后端的 friendAvatarUrl

    public ContactItem(String username, String displayName, String avatarUrl) {
        this.username = username;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
    }

    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public String getAvatarUrl() { return avatarUrl; }
}