package com.example.minichat.model;

/**
 * 这是一个“会话”的数据模型 (POJO)
 * 它代表“会话列表”中的一行
 */
public class Session {

    private String id;
    private String name;
    private String lastMessage;
    private String time;
    private String avatarUrl; // (未来使用)

    // 构造函数
    public Session(String id, String name, String lastMessage, String time) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.time = time;
    }

    // --- Getters ---
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
}
