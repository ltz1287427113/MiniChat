package com.example.minichat.viewmodel;

import android.app.Application; // 导入 Application
import androidx.annotation.NonNull; // 导入 NonNull
import androidx.lifecycle.AndroidViewModel; // [核心] 继承 AndroidViewModel
import androidx.lifecycle.LiveData;

import com.example.minichat.data.local.MessageEntity;
import com.example.minichat.data.repository.ChatRepository;

import java.util.List;

/**
 * 聊天界面的 ViewModel。
 * [核心] 它必须继承 AndroidViewModel 才能安全地持有 Application Context。
 */
public class ChatViewModel extends AndroidViewModel {

    // 1. 持有 Repository 的引用
    private ChatRepository repository;

    // 2. 持有 LiveData 的引用 (这是 UI 要观察的数据)
    private LiveData<List<MessageEntity>> allMessages;

    // 3. 构造函数
    public ChatViewModel(@NonNull Application application) {
        super(application);
        // 4. 获取 Repository 的单例实例
        repository = ChatRepository.getInstance(application);
        // 5. 从 Repository 获取数据源
        allMessages = repository.getAllMessages();
    }

    // --- 公开给 Activity(View) 的 API ---

    /**
     * 6. [给 Activity 调用] 获取所有消息。
     * Activity 将会“观察(observe)”这个 LiveData。
     */
    public LiveData<List<MessageEntity>> getMessages() {
        return allMessages;
    }

    /**
     * 7. [给 Activity 调用] 发送消息。
     * ViewModel 自身不处理逻辑，它只是“转发命令”给 Repository。
     */
    public void sendMessage(String content) {
        repository.sendMessage(content);
    }

    /**
     * 8. (可选) 用于我们测试“接收消息”功能
     */
    public void simulateReceiveMessage(String content) {
        repository.simulateReceiveMessage(content);
    }
}