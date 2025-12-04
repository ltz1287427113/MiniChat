package com.example.minichat.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.minichat.data.local.MessageEntity;
import com.example.minichat.data.model.response.ChatMessage;
import com.example.minichat.data.repository.ChatRepository;

import java.util.List;

public class ChatViewModel extends AndroidViewModel {

    private ChatRepository repository;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        repository = ChatRepository.getInstance(application);
    }

    // 1. 保存消息 (收到 WebSocket 消息时调用)
    public void saveMessageToLocal(ChatMessage message) {
        repository.saveReceivedMessage(message);
    }

    // 2. 获取当前会话的历史记录
    public LiveData<List<MessageEntity>> getChatHistory(int myId, int targetId, boolean isGroup) {
        if (isGroup) {
            return repository.getGroupChatHistory(targetId);
        } else {
            return repository.getPrivateChatHistory(myId, targetId);
        }
    }

    // [新] 获取所有会话列表
    public LiveData<List<MessageEntity>> getRecentSessionList(int myUserId) {
        return repository.getRecentSessions(myUserId); // 需在 Repository 中也加上对应方法
    }
}