package com.example.minichat.data.local;

import android.content.Context; // 导入 Context

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * 数据库主类（总管）。
 * * @Database 注解：
 * entities = {MessageEntity.class}：告诉 Room 这个数据库管理哪些“表”（实体类）。
 * version = 1：数据库的版本号。如果以后你修改了 "MessageEntity" (比如加了个新字段)，
 * 你就需要把版本号改成 2，并提供一个“迁移方案”(Migration)。
 * exportSchema = false：(可选) 禁用导出 schema，可以避免编译警告。
 */
@Database(entities = {MessageEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * 1. 告诉数据库，它拥有哪个 DAO（操作工具）。
     * Room 会自动为我们实现这个方法。
     */
    public abstract MessageDao messageDao();

    /**
     * 2. 我们将使用“单例模式”(Singleton) 来创建数据库。
     * 这能保证 App 在任何时候都只拥有 *一个* 数据库实例，避免资源浪费和冲突。
     * "volatile" 关键字确保多线程安全。
     */
    private static volatile AppDatabase INSTANCE;

    /**
     * 3. [核心] 获取数据库实例的静态方法。
     * 这是我们 App 中唯一获取数据库的入口。
     */
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            // 使用 "synchronized" 确保同一时间只有一个线程能创建实例
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    // 4. [核心] "创建" 数据库
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "mini_chat_database" // 数据库在手机上保存的文件名
                            )
                            // (可选，但在学习阶段很有用)
                            // 允许在主线程执行查询 (通常不推荐，但我们暂时为了简单)
                            // .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}