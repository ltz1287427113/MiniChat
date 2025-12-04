package com.example.minichat.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * [重要修改] 升级数据库版本到 2
 * 添加用户信息字段支持
 */
@Database(entities = {MessageEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MessageDao messageDao();

    private static volatile AppDatabase INSTANCE;

    // [新增] 数据库迁移策略：从版本1到版本2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // 添加新字段（允许为空，这样不会破坏现有数据）
            database.execSQL("ALTER TABLE messages_table ADD COLUMN senderNickname TEXT");
            database.execSQL("ALTER TABLE messages_table ADD COLUMN senderAvatarUrl TEXT");
            database.execSQL("ALTER TABLE messages_table ADD COLUMN receiverNickname TEXT");
            database.execSQL("ALTER TABLE messages_table ADD COLUMN receiverAvatarUrl TEXT");
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "mini_chat_database").addMigrations(MIGRATION_1_2) // [核心] 添加迁移策略
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}