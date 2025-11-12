package com.example.minichat.data.local;

import androidx.annotation.NonNull; // 导入这个
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 这是我们的数据库“表”定义。
 * Room 会根据这个类，自动创建一张名为 "messages_table" 的表。
 */
@Entity(tableName = "messages_table")
public class MessageEntity {

    /**
     * @PrimaryKey 告诉 Room 这是主键。
     * autoGenerate = true 让 Room 自动为我们生成 ID (例如 1, 2, 3...)。
     */
    @PrimaryKey(autoGenerate = true)
    @NonNull // 保证 ID 不为空
    public int id; // 消息的唯一 ID

    /**
     * @ColumnInfo (可选) 可以用来指定列名，但如果变量名相同，可以省略。
     */
    public String content; // 消息的文本内容

    public long timestamp; // 消息发送的时间戳 (方便排序)

    public String senderId; // 发送者 ID (例如 "me" 或 "user_123")

    // --- 构造函数 ---

    /**
     * Room 需要一个（通常是空的）构造函数来创建对象。
     * 我们写一个全参数的，Room 也能识别。
     */
    public MessageEntity(String content, long timestamp, String senderId) {
        this.content = content;
        this.timestamp = timestamp;
        this.senderId = senderId;
    }
}