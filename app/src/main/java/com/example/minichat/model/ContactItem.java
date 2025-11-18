package com.example.minichat.model;

/**
 * [注释]
 * 这是一个“数据模型”，用于表示一个联系人。
 */
public class ContactItem {

    private String id;
    private String name;
    private String avatarUrl; // (未来使用)

    public ContactItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
}