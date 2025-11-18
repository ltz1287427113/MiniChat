package com.example.minichat.fragment; // (确保这是你的包名)

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // [新导入]
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.minichat.R;
import com.example.minichat.activity.ChatDetailActivity;
import com.example.minichat.adapter.SessionAdapter;
import com.example.minichat.databinding.FragmentChatBinding;
import com.example.minichat.model.Session;
import com.example.minichat.utils.MenuHelper; // [新导入]

import java.util.ArrayList;
import java.util.List;

// [删除] 你不再需要 import android.widget.PopupMenu;
// [删除] 你不再需要 import java.lang.reflect.Method;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private SessionAdapter sessionAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 4. 初始化 RecyclerView (不变)
        setupRecyclerView();

        // 5. [核心修改]
        // 监听 Toolbar 上的菜单项点击
        binding.topNav.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_search) {
                Toast.makeText(getContext(), "点击了 搜索", Toast.LENGTH_SHORT).show();
                return true;

            } else if (itemId == R.id.menu_more) {
                // [关键] 当“更多”按钮被点击时...
                View anchorView = binding.topNav.findViewById(R.id.menu_more);

                // [新代码] 调用我们的 MenuHelper
                MenuHelper.showPopupMenuWithIcons(
                        requireActivity(),       // 1. Context
                        anchorView,              // 2. 锚点
                        R.menu.top_nav_menu,     // 3. 要加载的菜单
                        this::handleMenuClick    // 4. [新] 点击的回调方法
                );
                return true;
            }
            return false;
        });
    }

    /**
     * [注释]
     * [新方法]
     * 这是一个私有方法，专门用于处理弹窗菜单的点击事件。
     */
    private boolean handleMenuClick(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_scan) { //
            Toast.makeText(getContext(), "点击了 扫一扫", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.menu_add_friend) { //
            Toast.makeText(getContext(), "点击了 添加好友", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.menu_create_group) { //
            Toast.makeText(getContext(), "点击了 发起群聊", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
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
        DividerItemDecoration itemDecoration = new DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
        );
        binding.rvSessionList.addItemDecoration(itemDecoration);
        sessionAdapter.setOnSessionClickListener(session -> {
            Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
            intent.putExtra("CHAT_ID", session.getId());
            intent.putExtra("CHAT_NAME", session.getName());
            startActivity(intent);
        });
    }

    /**
     * [已删除]
     * 我们不再需要 showTopPopupMenu() 方法了！
     * 它已经被 MenuHelper.java 替代了。
     */
    // private void showTopPopupMenu(View anchorView) { ... }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}