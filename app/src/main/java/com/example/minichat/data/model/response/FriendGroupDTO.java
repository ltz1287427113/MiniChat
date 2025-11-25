package com.example.minichat.data.model.response;

import com.example.minichat.data.model.response.FriendDTO;

import java.util.List;

public class FriendGroupDTO {
    private String initial; // 分组字母，如 "A"
    private List<FriendDTO> friends; // 该组下的好友列表

    public String getInitial() { return initial; }
    public List<FriendDTO> getFriends() { return friends; }
}