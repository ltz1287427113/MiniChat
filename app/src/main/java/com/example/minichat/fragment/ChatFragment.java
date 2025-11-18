package com.example.minichat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.minichat.R;
import com.example.minichat.activity.ChatDetailActivity;
import com.example.minichat.adapter.SessionAdapter;
import com.example.minichat.databinding.FragmentChatBinding;
import com.example.minichat.model.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * [注释]
 * 这是一个“精简版”的 ChatFragment。
 * 它不再处理 Toolbar 逻辑，因为 MainActivity 已经接管了。
 */
public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private SessionAdapter sessionAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // [修改] 加载的是 *简化的* fragment_chat.xml
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 4. 初始化 RecyclerView (不变)
        setupRecyclerView();

        // 5. [已删除]
        // binding.topNav.setOnMenuItemClickListener(...)
        // 逻辑已移至 MainActivity
    }

    private void setupRecyclerView() {
        // ... (此方法完全不变) ...
        List<Session> fakeSessions = new ArrayList<>();
        fakeSessions.add(new Session("user_123", "张三", "好的，没问题！", "昨天 10:20"));
        fakeSessions.add(new Session("user_456", "李四", "[图片]", "周一 08:30"));
        fakeSessions.add(new Session("group_789", "技术交流群", "快来人，出 Bug 了！", "10:15"));
        // ... (你其他的假数据)

        sessionAdapter = new SessionAdapter(fakeSessions);
        binding.rvSessionList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSessionList.setAdapter(sessionAdapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL);
        binding.rvSessionList.addItemDecoration(itemDecoration);
        sessionAdapter.setOnSessionClickListener(session -> {
            Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
            intent.putExtra("CHAT_ID", session.getId());
            intent.putExtra("CHAT_NAME", session.getName());
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}