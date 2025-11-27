package com.example.minichat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minichat.R;
import com.example.minichat.data.model.response.ApplicationResponse;

import java.util.List;

public class NewFriendsAdapter extends RecyclerView.Adapter<NewFriendsAdapter.ViewHolder> {

    public interface OnAcceptButtonClickListener {
        void onAcceptButtonClick(ApplicationResponse item);
    }

    private List<ApplicationResponse> list;
    private OnAcceptButtonClickListener listener;

    public NewFriendsAdapter(List<ApplicationResponse> list, OnAcceptButtonClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public void updateList(List<ApplicationResponse> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ApplicationResponse item = list.get(position);

        holder.tvNickname.setText(item.getNickname());
        // 如果是别人申请我，显示 message；如果是我申请别人，也可以显示 message
        holder.tvReason.setText(item.getMessage());

        // 处理状态
        String status = item.getStatus(); // "PENDING", "ACCEPTED", "REJECTED"

        if ("ACCEPTED".equals(status)) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText("已添加");
            holder.btnAccept.setVisibility(View.GONE);
        } else if ("REJECTED".equals(status)) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText("已拒绝"); // 或者 "已过期"
            holder.btnAccept.setVisibility(View.GONE);
        } else {
            // PENDING 状态
            if (item.isApplicantIsMe()) {
                // 我申请别人（等待验证）
                holder.tvStatus.setVisibility(View.VISIBLE);
                holder.tvStatus.setText("等待验证");
                holder.btnAccept.setVisibility(View.GONE);
            } else {
                // 别人申请我（显示接受按钮）
                holder.tvStatus.setVisibility(View.GONE);
                holder.btnAccept.setVisibility(View.VISIBLE);

                holder.btnAccept.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onAcceptButtonClick(item);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNickname, tvReason, tvStatus;
        ImageView ivAvatar;
        Button btnAccept;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNickname = itemView.findViewById(R.id.tv_nickname);
            tvReason = itemView.findViewById(R.id.tv_reason);
            tvStatus = itemView.findViewById(R.id.tv_status);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            btnAccept = itemView.findViewById(R.id.btn_accept);
        }
    }
}