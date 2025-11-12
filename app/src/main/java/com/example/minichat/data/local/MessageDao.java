package com.example.minichat.data.local;

import androidx.lifecycle.LiveData; // [重要] 导入 LiveData
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * 数据访问对象 (DAO)。
 * Room 要求我们为 DAO 创建一个接口。
 */
@Dao
public interface MessageDao {

    /**
     * @Insert 告诉 Room 这是一个“插入”操作。
     * onConflict = OnConflictStrategy.REPLACE：
     * 如果插入的数据和已有的数据冲突 (例如 id 相同)，就用新的替换旧的。
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessage(MessageEntity message); // 插入一条消息

    /**
     * @Query 告诉 Room 这是一个“查询”操作。
     * "SELECT * FROM messages_table ORDER BY timestamp ASC"：
     * 这是一句 SQL 语句，意思是：
     * 1. SELECT * ： 选中所有列
     * 2. FROM messages_table ： 从 "messages_table" 这张表
     * 3. ORDER BY timestamp ASC ： 按照 "timestamp" (时间戳) 升序 (ASC) 排列
     *
     * [核心] LiveData<List<MessageEntity>>：
     * 我们返回一个 LiveData。Room 会“观察”这张表。
     * 任何时候，只要 "messages_table" 里的数据变化了 (比如插入了新消息)，
     * Room 就会自动更新这个 LiveData，我们的 UI 也会自动刷新！
     */
    @Query("SELECT * FROM messages_table ORDER BY timestamp ASC")
    LiveData<List<MessageEntity>> getAllMessages();

    // (未来) 我们可以添加更复杂的查询，比如只查询和特定好友的聊天记录：
    // @Query("SELECT * FROM messages_table WHERE senderId = :friendId OR receiverId = :friendId ORDER BY timestamp ASC")
    // LiveData<List<MessageEntity>> getMessagesWithFriend(String friendId);
}
