package com.example.minichat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minichat.R;
import com.example.minichat.model.Session;
import com.example.minichat.utils.UserDisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * [改进版] 会话列表适配器
 * 1. 支持头像显示
 * 2. 优化数据更新
 */
public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    private List<Session> sessions = new ArrayList<>();
    private OnSessionClickListener listener;

    public SessionAdapter(List<Session> sessions) {
        this.sessions = sessions != null ? sessions : new ArrayList<>();
    }

    public void setOnSessionClickListener(OnSessionClickListener listener) {
        this.listener = listener;
    }

    /**
     * [新增] 更新数据的方法
     */
    public void updateSessions(List<Session> newSessions) {
        this.sessions.clear();
        if (newSessions != null) {
            this.sessions.addAll(newSessions);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Session session = sessions.get(position);
        holder.bind(session);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSessionClick(session);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLastMessage, tvTime;
        ImageView ivAvatar;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_contact_name);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvTime = itemView.findViewById(R.id.tv_last_time);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
        }

        void bind(Session session) {
            tvName.setText(session.getName());
            tvLastMessage.setText(session.getLastMessage());
            tvTime.setText(session.getTime());

            // [新增] 加载头像
            if (session.getAvatarUrl() != null && !session.getAvatarUrl().isEmpty()) {
                UserDisplayUtils.loadAvatar(itemView.getContext(), session.getAvatarUrl(), ivAvatar);
            }
        }
    }

    public interface OnSessionClickListener {
        void onSessionClick(Session session);
    }
}