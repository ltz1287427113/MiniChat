package com.example.minichat.model;

/**
 * [注释]
 * 这是一个“数据模型”，用于表示一个功能项 (如 "新的朋友")。
 */
public class FunctionItem {

    private String Nikename;
    private int iconResId;

    public FunctionItem(String name, int iconResId) {
        this.Nikename = name;
        this.iconResId = iconResId;
    }

    // Getters
    public String getName() { return Nikename; }
    public int getIconResId() { return iconResId; }
}
