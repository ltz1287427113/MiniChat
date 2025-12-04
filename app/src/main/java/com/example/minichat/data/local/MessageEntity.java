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

    // [新增] 用户信息字段 - 用于快速显示UI
    public String senderNickname;   // 发送者昵称
    public String senderAvatarUrl;  // 发送者头像
    public String receiverNickname; // 接收者昵称
    public String receiverAvatarUrl;// 接收者头像

    public MessageEntity() {
    }

    // 辅助构造函数：从网络消息转为本地消息
    public MessageEntity(ChatMessage dto) {
        this.chatMessageId = dto.getChatMessageId();
        this.senderId = dto.getSenderId();
        this.receiverId = dto.getReceiverId();
        this.groupId = dto.getGroupId();
        this.messageType = dto.getMessageType() != null ? dto.getMessageType().name() : "PRIVATE_MESSAGE";
        this.content = dto.getContent();
        this.createAt = dto.getCreateAt();
        // 注意：用户信息需要后续补充
    }

    // [新增] Getter方法
    public String getSenderNickname() {
        return senderNickname;
    }

    public String getSenderAvatarUrl() {
        return senderAvatarUrl;
    }

    public String getReceiverNickname() {
        return receiverNickname;
    }

    public String getReceiverAvatarUrl() {
        return receiverAvatarUrl;
    }

    // [新增] Setter方法
    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    public void setSenderAvatarUrl(String senderAvatarUrl) {
        this.senderAvatarUrl = senderAvatarUrl;
    }

    public void setReceiverNickname(String receiverNickname) {
        this.receiverNickname = receiverNickname;
    }

    public void setReceiverAvatarUrl(String receiverAvatarUrl) {
        this.receiverAvatarUrl = receiverAvatarUrl;
    }
}