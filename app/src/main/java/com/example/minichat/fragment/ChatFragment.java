package com.example.minichat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.minichat.activity.ChatDetailActivity;
import com.example.minichat.adapter.SessionAdapter;
import com.example.minichat.data.local.MessageEntity;
import com.example.minichat.databinding.FragmentChatBinding;
import com.example.minichat.model.Session;
import com.example.minichat.utils.SpUtils;
import com.example.minichat.viewmodel.ChatViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private SessionAdapter sessionAdapter;
    private ChatViewModel viewModel;
    private int myUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 获取自己的 ID
        myUserId = SpUtils.getUser(getContext()).getUserId();

        // 初始化列表 (先给个空的)
        setupRecyclerView();

        // 初始化 ViewModel
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        // [核心] 观察数据库中的会话列表
        viewModel.getRecentSessionList(myUserId).observe(getViewLifecycleOwner(), messageEntities -> {
            // 将 MessageEntity 转换为 Session 对象
            List<Session> sessions = convertToSessions(messageEntities);

            // 更新 UI
            // (SessionAdapter 需要添加一个 updateData 方法，或者重新 setAdapter)
            sessionAdapter = new SessionAdapter(sessions);
            binding.rvSessionList.setAdapter(sessionAdapter);
            setupClickEvent(); // 重新绑定点击事件
        });
    }

    private void setupRecyclerView() {
        binding.rvSessionList.setLayoutManager(new LinearLayoutManager(getContext()));
        sessionAdapter = new SessionAdapter(new ArrayList<>());
        binding.rvSessionList.setAdapter(sessionAdapter);
    }

    private void setupClickEvent() {
        sessionAdapter.setOnSessionClickListener(session -> {
            Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
            intent.putExtra("CHAT_ID", session.getId());     // 传 ID (String)
            intent.putExtra("CHAT_NAME", session.getName()); // 传名字
            startActivity(intent);
        });
    }

    /**
     * 将数据库消息转换为会话列表 UI 模型
     */
    private List<Session> convertToSessions(List<MessageEntity> messages) {
        List<Session> list = new ArrayList<>();
        for (MessageEntity msg : messages) {
            // 判断对方是谁
            int otherId = (msg.senderId == myUserId) ? msg.receiverId : msg.senderId;

            // TODO: 这里我们暂时只有 ID，没有对方的名字和头像
            // 实际项目中需要查 User 表。现在先用 ID 代替名字。
            String name = "用户 " + otherId;

            list.add(new Session(
                    String.valueOf(otherId),
                    name,
                    msg.content,
                    msg.createAt
            ));
        }
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}