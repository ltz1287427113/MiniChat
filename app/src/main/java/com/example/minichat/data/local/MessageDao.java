package com.example.minichat.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessage(MessageEntity message);

    // 1. 查询私聊记录：(我发给他的) OR (他发给我的)
    @Query("SELECT * FROM messages_table " +
            "WHERE (senderId = :myId AND receiverId = :friendId) " +
            "   OR (senderId = :friendId AND receiverId = :myId) " +
            "ORDER BY id ASC")
    LiveData<List<MessageEntity>> getPrivateMessages(int myId, int friendId);

    // 2. 查询群聊记录：所有发到这个群的消息
    @Query("SELECT * FROM messages_table WHERE groupId = :groupId ORDER BY id ASC")
    LiveData<List<MessageEntity>> getGroupMessages(int groupId);

    // (保留旧方法以防报错，但建议不再使用)
    @Query("SELECT * FROM messages_table ORDER BY id ASC")
    LiveData<List<MessageEntity>> getAllMessages();
    /**
     * [新] 获取最近会话列表
     * 逻辑：
     * 1. 找出所有与我 (myId) 相关的消息
     * 2. 按照“对方ID” (chatPartnerId) 分组
     * 3. 取每组 timestamp 最大的那条
     * 4. 按时间倒序排列
     */
    @Query("SELECT * FROM messages_table " +
            "WHERE id IN (" +
            "  SELECT MAX(id) FROM messages_table " +
            "  WHERE senderId = :myId OR receiverId = :myId " +
            "  GROUP BY CASE WHEN senderId = :myId THEN receiverId ELSE senderId END" +
            ") " +
            "ORDER BY createAt DESC")
    LiveData<List<MessageEntity>> getRecentSessions(int myId);
}