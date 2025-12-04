package com.example.minichat.data.repository;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.minichat.data.local.AppDatabase;
import com.example.minichat.data.local.MessageDao;
import com.example.minichat.data.local.MessageEntity;
import com.example.minichat.data.model.response.ChatMessage;
import com.example.minichat.utils.SpUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * [重要改进] ChatRepository
 * 1. 保存消息时自动补充用户信息
 * 2. 处理头像和昵称数据
 */
public class ChatRepository {

    private MessageDao messageDao;
    private Context context;
    private static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();
    private static volatile ChatRepository INSTANCE;

    private ChatRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        messageDao = database.messageDao();
        context = application.getApplicationContext();
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

    /**
     * [核心改进] 保存收到的消息，自动补充用户信息
     */
    public void saveReceivedMessage(ChatMessage chatMessage) {
        MessageEntity entity = new MessageEntity(chatMessage);

        // [关键] 补充用户信息
        enrichUserInfo(entity);

        databaseWriteExecutor.execute(() -> {
            messageDao.insertMessage(entity);
        });
    }

    /**
     * [新增] 补充消息的用户信息
     * 从本地缓存或网络获取用户头像和昵称
     */
    private void enrichUserInfo(MessageEntity entity) {
        // 获取当前登录用户信息
        var currentUser = SpUtils.getUser(context);
        if (currentUser == null) return;

        int myUserId = currentUser.getUserId();

        // [逻辑] 如果发送者是我，填充我的信息
        if (entity.senderId != null && entity.senderId == myUserId) {
            entity.setSenderNickname(currentUser.getNickname());
            entity.setSenderAvatarUrl(currentUser.getAvatarUrl());
        }

        // [逻辑] 如果接收者是我，填充我的信息
        if (entity.receiverId != null && entity.receiverId == myUserId) {
            entity.setReceiverNickname(currentUser.getNickname());
            entity.setReceiverAvatarUrl(currentUser.getAvatarUrl());
        }

        // TODO: 对于对方的信息，理想情况下应该：
        // 1. 从本地缓存的好友列表中查找
        // 2. 如果没有，发起网络请求获取
        // 简化版本：暂时留空，由UI层处理
    }

    public LiveData<List<MessageEntity>> getPrivateChatHistory(int myId, int friendId) {
        return messageDao.getPrivateMessages(myId, friendId);
    }

    public LiveData<List<MessageEntity>> getGroupChatHistory(int groupId) {
        return messageDao.getGroupMessages(groupId);
    }

    public LiveData<List<MessageEntity>> getRecentSessions(int myId) {
        return messageDao.getRecentSessions(myId);
    }
}