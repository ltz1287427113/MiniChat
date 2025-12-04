package com.example.minichat.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.minichat.data.local.AppDatabase;
import com.example.minichat.data.local.MessageDao;
import com.example.minichat.data.local.MessageEntity;
import com.example.minichat.data.model.response.ChatMessage;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatRepository {

    private MessageDao messageDao;
    private static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();
    private static volatile ChatRepository INSTANCE;

    private ChatRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        messageDao = database.messageDao();
    }

    public static ChatRepository getInstance(final Application application) {
        if (INSTANCE == null) {
            synchronized (ChatRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ChatRepository(application);
                }
            }
        }
        return INSTANCE;
    }

    // --- 核心功能 ---

    // 1. 保存收到的消息 (网络 -> 本地)
    public void saveReceivedMessage(ChatMessage chatMessage) {
        MessageEntity entity = new MessageEntity(chatMessage);
        databaseWriteExecutor.execute(() -> {
            messageDao.insertMessage(entity);
        });
    }

    // 2. 获取私聊历史
    public LiveData<List<MessageEntity>> getPrivateChatHistory(int myId, int friendId) {
        return messageDao.getPrivateMessages(myId, friendId);
    }

    // 3. 获取群聊历史
    public LiveData<List<MessageEntity>> getGroupChatHistory(int groupId) {
        return messageDao.getGroupMessages(groupId);
    }
    public LiveData<List<MessageEntity>> getRecentSessions(int myId) {
        return messageDao.getRecentSessions(myId);
    }
}