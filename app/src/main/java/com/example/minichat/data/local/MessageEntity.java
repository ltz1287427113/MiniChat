package com.example.minichat.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.minichat.data.model.response.ChatMessage;

@Entity(tableName = "messages_table")
public class MessageEntity {

    @PrimaryKey(autoGenerate = true)
    public int id; // 本地主键

    // --- 对应后端字段 ---
    public Integer chatMessageId; // 服务端ID
    public Integer senderId;      // 发送者ID
    public Integer receiverId;    // 接收者ID
    public Integer groupId;       // 群聊ID
    public String messageType;    // "PRIVATE_MESSAGE" 或 "GROUP_MESSAGE"
    public String content;        // 内容
    public String createAt;       // 时间

    public MessageEntity() {}

    // 辅助构造函数：从网络消息转为本地消息
    public MessageEntity(ChatMessage dto) {
        this.chatMessageId = dto.getChatMessageId();
        this.senderId = dto.getSenderId();
        this.receiverId = dto.getReceiverId();
        this.groupId = dto.getGroupId();
        // 将枚举转为字符串存储
        this.messageType = dto.getMessageType() != null ? dto.getMessageType().name() : "PRIVATE_MESSAGE";
        this.content = dto.getContent();
        this.createAt = dto.getCreateAt();
    }
}