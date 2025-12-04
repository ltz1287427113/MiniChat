package com.example.minichat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minichat.R;
import com.example.minichat.data.local.MessageEntity; // [导入] 我们的数据库实体
import com.example.minichat.utils.UserDisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 这是一个“聊天气泡”的适配器
 * 它显示 MessageEntity 列表
 */
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 1. 定义两种视图类型
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<MessageEntity> messages = new ArrayList<>();
    private int myUserId;

    public MessageAdapter(int myUserId) {
        this.myUserId = myUserId;
    }
    // 2. [核心] 根据 senderId 判断布局
    @Override
    public int getItemViewType(int position) {
        MessageEntity message = messages.get(position);
        // (这是我们之前在 ChatViewModel 中约定的)
        if (message.senderId != null && message.senderId == myUserId) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    // 3. 创建两种布局
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    // 4. 绑定数据
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageEntity message = messages.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // 5. [核心] 更新数据的方法
    public void setMessages(List<MessageEntity> messages) {
        this.messages = messages;
        notifyDataSetChanged(); // 刷新列表
    }

    // --- 两个 ViewHolder ---

    // --- 发送消息的ViewHolder ---
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageContent;
        ImageView avatarView;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.tv_message_content);
            avatarView = itemView.findViewById(R.id.iv_avatar_sent);
        }

        void bind(MessageEntity message) {
            messageContent.setText(message.content);

            // [核心] 加载发送者（我）的头像
            if (message.senderAvatarUrl != null) {
                UserDisplayUtils.loadAvatar(
                        itemView.getContext(),
                        message.senderAvatarUrl,
                        avatarView
                );
            }
        }
    }

    // --- 接收消息的ViewHolder ---
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageContent;
        ImageView avatarView;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.tv_message_content);
            avatarView = itemView.findViewById(R.id.iv_avatar_received);
        }

        void bind(MessageEntity message) {
            messageContent.setText(message.content);

            // [核心] 加载发送者（对方）的头像
            if (message.senderAvatarUrl != null) {
                UserDisplayUtils.loadAvatar(
                        itemView.getContext(),
                        message.senderAvatarUrl,
                        avatarView
                );
            }
        }
    }
}