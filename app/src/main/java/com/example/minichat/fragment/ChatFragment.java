package com.example.minichat.fragment; // (确保这是你的包名)

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem; // [导入]
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu; // [导入]
import android.widget.Toast; // [导入]

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.minichat.R;
import com.example.minichat.activity.ChatDetailActivity;
import com.example.minichat.adapter.SessionAdapter;
import com.example.minichat.databinding.FragmentChatBinding; // [导入]
import com.example.minichat.model.Session;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    // 1. 添加 ViewBinding 引用
    private FragmentChatBinding binding;
    // 2. 新的 Adapter
    private SessionAdapter sessionAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 2. [修改] 使用 ViewBinding 加载布局
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 4. 初始化 RecyclerView
        setupRecyclerView();

        // 5. [核心修改]
        // 删除旧的 findViewById 监听器
        // binding.topNav.findViewById(R.id.iv_top_menu).setOnClickListener(this::showTopPopupMenu);

        // [新代码] 监听 Toolbar 上的菜单项点击
        binding.topNav.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_search) {
                Toast.makeText(getContext(), "点击了 搜索", Toast.LENGTH_SHORT).show();
                return true;

            } else if (itemId == R.id.menu_more) {
                // [关键] 当“更多”按钮被点击时...
                // 找到“更多”按钮这个 View，作为弹窗的“锚点”
                View anchorView = binding.topNav.findViewById(R.id.menu_more);

                // ...[调用你完全相同的旧方法]
                showTopPopupMenu(anchorView);
                return true;
            }

            return false;
        });

        // 6. (未来) 在这里初始化 ViewModel，并观察(observe)会话列表
    }

    private void setupRecyclerView() {
        // 7. [核心] 创建假数据
        List<Session> fakeSessions = new ArrayList<>();
        fakeSessions.add(new Session("user_123", "张三", "好的，没问题！", "昨天 10:20"));
        fakeSessions.add(new Session("user_456", "李四", "[图片]", "周一 08:30"));
        fakeSessions.add(new Session("group_789", "技术交流群", "快来人，出 Bug 了！", "10:15"));
        fakeSessions.add(new Session("group_789", "技术交流群", "快来人，出 Bug 了！", "10:15"));
        fakeSessions.add(new Session("group_789", "技术交流群", "快来人，出 Bug 了！", "10:15"));
        fakeSessions.add(new Session("group_789", "技术交流群", "快来人，出 Bug 了！", "10:15"));
        fakeSessions.add(new Session("group_789", "技术交流群", "快来人，出 Bug 了！", "10:15"));
        fakeSessions.add(new Session("group_789", "技术交流群", "快来人，出 Bug 了！", "10:15"));
        fakeSessions.add(new Session("group_789", "技术交流群", "快来人，出 Bug 了！", "10:15"));
        fakeSessions.add(new Session("group_789", "技术交流群", "快来人，出 Bug 了！", "10:15"));
        fakeSessions.add(new Session("group_789", "技术交流群", "快来人，出 Bug 了！", "10:15"));
        fakeSessions.add(new Session("group_789", "技术交流群", "快来人，出 Bug 了！", "10:15"));
        fakeSessions.add(new Session("group_789", "技术交流群", "快来人，出 Bug 了！", "10:15"));

        // 8. [核心] 初始化 SessionAdapter
        sessionAdapter = new SessionAdapter(fakeSessions);
        binding.rvSessionList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSessionList.setAdapter(sessionAdapter);

        // --- [在这里添加新代码] ---

        // 9. 创建一个默认的分割线
        DividerItemDecoration itemDecoration = new DividerItemDecoration(
                requireContext(), // 使用 Context
                LinearLayoutManager.VERTICAL // 指定方向为垂直
        );

        // 10. 将分割线添加到 RecyclerView
        binding.rvSessionList.addItemDecoration(itemDecoration);

        // --- [新代码结束] ---

        // 11. [核心] 设置列表项的点击事件
        sessionAdapter.setOnSessionClickListener(session -> {
            // ... (你的跳转逻辑)
            Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
            intent.putExtra("CHAT_ID", session.getId());
            intent.putExtra("CHAT_NAME", session.getName());
            startActivity(intent);
        });
    }

    /**
     * 显示顶部的弹窗菜单
     * @param anchorView 点击的那个 ImageView，菜单会“锚定”在它下面
     */
    private void showTopPopupMenu(View anchorView) {

        // [修改：看这里] 我们暂时先不管宽度，先只创建 PopupMenu
        PopupMenu popup = new PopupMenu(requireActivity(), anchorView);

        // 2. 将 menu.xml 文件“充气”到弹窗中
        popup.getMenuInflater().inflate(R.menu.top_nav_menu, popup.getMenu());

        // 3. [新代码：强制显示图标的 HACK]
        // ------------------------------------
        try {
            // 使用“反射”来调用一个隐藏的方法
            Method setOptionalIconsVisible = popup.getMenu().getClass().getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            setOptionalIconsVisible.setAccessible(true);
            setOptionalIconsVisible.invoke(popup.getMenu(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ------------------------------------

        // 4. 设置菜单项的点击事件 (保持不变)
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_scan) {
                    Toast.makeText(getContext(), "点击了 扫一扫", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.menu_add_friend) {
                    Toast.makeText(getContext(), "点击了 添加好友", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.menu_create_group) {
                    Toast.makeText(getContext(), "点击了 发起群聊", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }
            }
        });

        // 5. 显示弹窗
        popup.show();
    }

    // 5. [新方法] 添加 onDestroyView 来防止内存泄漏
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 释放 binding
    }
}