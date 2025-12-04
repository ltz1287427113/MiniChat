package com.example.minichat.model;

/**
 * [改进版] 会话数据模型
 * 添加更多必要字段
 */
public class Session {

    private String id;           // 会话ID（对方用户ID）
    private String name;         // 显示名称（备注或昵称）
    private String lastMessage;  // 最后一条消息
    private String time;         // 时间
    private String avatarUrl;    // [新增] 头像URL
    private int unreadCount;     // [新增] 未读数量（可选）

    // 构造函数
    public Session(String id, String name, String lastMessage, String time) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.time = time;
    }

    // [新增] 完整构造函数
    public Session(String id, String name, String lastMessage, String time, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.time = time;
        this.avatarUrl = avatarUrl;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getTime() {
        return time;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    // Setters
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}