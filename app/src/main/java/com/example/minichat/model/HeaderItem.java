package com.example.minichat.model;

/**
 * [注释]
 * 这是一个“数据模型”，用于表示一个字母标题 (如 "A")。
 */
public class HeaderItem {

    private String letter;

    public HeaderItem(String letter) {
        this.letter = letter;
    }

    public String getLetter() {
        return letter;
    }
}
