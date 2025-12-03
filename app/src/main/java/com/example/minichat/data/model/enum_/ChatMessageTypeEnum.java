package com.example.minichat.data.model.enum_;

/**
 * 消息类型枚举
 * 作用：区分这条消息是 私聊(一对一) 还是 群聊(多对多)
 * 必须和后端的枚举名字完全一致，否则 JSON 解析会报错
 */
public enum ChatMessageTypeEnum {
    PRIVATE_MESSAGE, // 私聊
    GROUP_MESSAGE    // 群聊
}