package com.example.minichat.data.repository;

import android.app.Application; // [注意] 导入 Application
import androidx.lifecycle.LiveData;

import com.example.minichat.data.local.AppDatabase;
import com.example.minichat.data.local.MessageDao;
import com.example.minichat.data.local.MessageEntity;

import java.util.List;
import java.util.concurrent.ExecutorService; // 导入线程池
import java.util.concurrent.Executors;     // 导入线程池

/**
 * 数据仓库 (Repository)。
 * 负责处理数据请求，并决定是从本地 (Room) 还是网络获取数据。
 * 这是 ViewModel 唯一的数据来源。
 */
public class ChatRepository {

    // 1. 数据源：DAO (来自 Room)
    private MessageDao messageDao;

    // 2. 数据缓存/来源：LiveData (来自 Room)
    private LiveData<List<MessageEntity>> allMessages;

    // 3. [核心] 创建一个后台线程池 (我们用它来执行数据库“写入”操作)
    // 我们只使用一个线程来保证插入操作的顺序
    private static final ExecutorService databaseWriteExecutor =
            Executors.newSingleThreadExecutor();

    // 4. 单例模式 (确保 App 只有一个 Repository 实例)
    private static volatile ChatRepository INSTANCE;

    // 5. 构造函数 (私有)
    private ChatRepository(Application application) {
        // a. 获取数据库实例
        AppDatabase database = AppDatabase.getDatabase(application);
        // b. 获取 DAO
        messageDao = database.messageDao();
        // c. 获取 LiveData 数据源 (Room 会自动在后台线程处理这个查询)
        allMessages = messageDao.getAllMessages();
    }

    // 6. 获取单例的公开方法
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

    // --- 公开给 ViewModel 的 API ---

    /**
     * 7. [给 ViewModel 调用] 获取所有消息。
     * 因为我们返回的是 LiveData，所以它天生就是异步的。
     * ViewModel 只需要观察(observe)它即可。
     */
    public LiveData<List<MessageEntity>> getAllMessages() {
        return allMessages;
    }

    /**
     * 8. [给 ViewModel 调用] 发送一条新消息 (插入到数据库)。
     * @param content 消息的文本内容
     */
    public void sendMessage(String content) {
        // a. 创建消息实体
        MessageEntity newMessage = new MessageEntity(
                content,
                System.currentTimeMillis(),
                "me" // [阶段一] 我们暂时假定发送者都是 "me"
        );

        // b. [核心] 必须在“后台线程”执行数据库写入
        databaseWriteExecutor.execute(() -> {
            messageDao.insertMessage(newMessage);
        });
    }

    // (未来) 模拟接收消息
    public void simulateReceiveMessage(String content) {
        MessageEntity receivedMessage = new MessageEntity(
                content,
                System.currentTimeMillis(),
                "other" // 模拟对方
        );
        databaseWriteExecutor.execute(() -> {
            messageDao.insertMessage(receivedMessage);
        });
    }
}