package com.example.minichat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.minichat.R;
import com.example.minichat.model.Session; // [导入我们自己的 Session]
import java.util.List;

/**
 * 这是一个“愚蠢的”适配器。它只负责显示 List<Session>。
 * 它不调用 API，也不处理业务逻辑。
 */
public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    // 数据源
    private List<Session> sessions;

    // (点击事件的回调接口)
    private OnSessionClickListener listener;

    // (构造函数，接收数据)
    public SessionAdapter(List<Session> sessions) {
        this.sessions = sessions;
    }

    // (设置点击监听)
    public void setOnSessionClickListener(OnSessionClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Session session = sessions.get(position);
        holder.bind(session);

        // 绑定点击事件
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

    // --- ViewHolder ---
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
        }
    }

    // --- 点击事件接口 ---
    public interface OnSessionClickListener {
        void onSessionClick(Session session);
    }
}