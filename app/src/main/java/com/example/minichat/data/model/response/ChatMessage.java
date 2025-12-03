package com.example.minichat.data.model.response;

import com.example.minichat.data.model.enum_.ChatMessageTypeEnum;

public class ChatMessage {
    // 对应后端的 chatMessageId (消息的身份证号)
    private Integer chatMessageId;

    // 发送者 ID
    private Integer senderId;

    // 接收者 ID (私聊时有用)
    private Integer receiverId;

    // 群组 ID (群聊时有用)
    private Integer groupId;

    // 消息类型 (私聊/群聊)
    private ChatMessageTypeEnum messageType;

    // 消息的文本内容
    private String content;

    // 发送时间 (后端发过来是字符串，我们先用 String 接收，不容易出错)
    private String createAt;

    // --- 构造函数 (用于我们发送消息时创建对象) ---
    public ChatMessage(Integer receiverId, Integer groupId, ChatMessageTypeEnum messageType, String content) {
        this.receiverId = receiverId;
        this.groupId = groupId;
        this.messageType = messageType;
        this.content = content;
        // senderId, chatMessageId, createAt 不需要我们填，后端会处理
    }

    // --- Getter 和 Setter 方法 (必须有，Gson 解析需要) ---
    // 你可以使用 Alt+Insert 快捷键自动生成，或者直接复制下面的

    public Integer getChatMessageId() { return chatMessageId; }
    public void setChatMessageId(Integer chatMessageId) { this.chatMessageId = chatMessageId; }

    public Integer getSenderId() { return senderId; }
    public void setSenderId(Integer senderId) { this.senderId = senderId; }

    public Integer getReceiverId() { return receiverId; }
    public void setReceiverId(Integer receiverId) { this.receiverId = receiverId; }

    public Integer getGroupId() { return groupId; }
    public void setGroupId(Integer groupId) { this.groupId = groupId; }

    public ChatMessageTypeEnum getMessageType() { return messageType; }
    public void setMessageType(ChatMessageTypeEnum messageType) { this.messageType = messageType; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreateAt() { return createAt; }
    public void setCreateAt(String createAt) { this.createAt = createAt; }
}