package com.example.minichat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.minichat.R;
import com.example.minichat.model.ContactItem; // 复用之前的 ContactItem 模型

import java.util.List;

public class ChatMemberAdapter extends RecyclerView.Adapter<ChatMemberAdapter.MemberViewHolder> {

    private List<ContactItem> members;

    public ChatMemberAdapter(List<ContactItem> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_info_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        // [核心逻辑]
        // 如果是最后一个位置 (position == members.size())，显示“加号”
        // 否则显示成员信息
        if (position == members.size()) {
            // --- 显示加号 ---
            holder.ivAvatar.setImageResource(R.drawable.ic_add_group_member); // 使用系统加号图标，或者你自己的
            holder.tvName.setText(""); // 加号没有名字

            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "点击了加号 (邀请成员)", Toast.LENGTH_SHORT).show();
            });
        } else {
            // --- 显示成员 ---
            ContactItem member = members.get(position);
            // 使用 UserDisplayUtils 加载头像
            com.example.minichat.utils.UserDisplayUtils.loadAvatar(holder.itemView.getContext(), member.getAvatarUrl(), holder.ivAvatar);
            holder.ivAvatar.setBackground(null); // 清除背景
            holder.tvName.setText(member.getDisplayName());

            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "点击了成员: " + member.getDisplayName(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount() {
        // [核心] 总数 = 成员数 + 1个加号
        return members.size() + 1;
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}