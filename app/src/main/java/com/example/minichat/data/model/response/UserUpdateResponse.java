package com.example.minichat.data.model.response;

public class UserUpdateResponse {
    private int userId;
    private String username;
    private String email;
    private String nickname;
    private String avatarUrl;

    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getNickname() { return nickname; }
    public String getAvatarUrl() { return avatarUrl; }
}
