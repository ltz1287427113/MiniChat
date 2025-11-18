package com.example.minichat.fragment; // (确保这是你的包名)

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem; // [新导入]
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast; // [新导入]

import androidx.annotation.NonNull; // [新导入]
import androidx.annotation.Nullable; // [新导入]
import androidx.fragment.app.Fragment;

import com.example.minichat.R;
import com.example.minichat.databinding.FragmentDiscoverBinding; // [新导入]
import com.example.minichat.utils.MenuHelper; // [新导入]

/**
 * [注释]
 * 这是“发现”标签页的 Fragment。
 * 它现在使用 ViewBinding 并处理 Toolbar 菜单点击。
 */
public class DiscoverFragment extends Fragment {

    // 1. [新] 添加 ViewBinding
    private FragmentDiscoverBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // 2. [修改] 使用 ViewBinding 加载布局
        binding = FragmentDiscoverBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 3. [新] 为“朋友圈”按钮添加点击事件
        binding.btnFriendCircle.setOnClickListener(v -> {
            Toast.makeText(getContext(), "点击了 朋友圈", Toast.LENGTH_SHORT).show();
            // TODO: 在这里跳转到朋友圈 Activity
        });

        // 5. [新] 设置 Toolbar 上的点击事件 (复用 ChatFragment 的逻辑)
        binding.topNav.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_search) {
                Toast.makeText(getContext(), "点击了 搜索", Toast.LENGTH_SHORT).show();
                return true;

            } else if (itemId == R.id.menu_more) {
                // [关键] 当“更多”按钮被点击时...
                View anchorView = binding.topNav.findViewById(R.id.menu_more);

                // [新代码] 调用我们的可复用工具类 MenuHelper
                MenuHelper.showPopupMenuWithIcons(requireActivity(),       // 1. Context
                        anchorView,              // 2. 锚点
                        R.menu.top_nav_menu,     // 3. 加载菜单
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
     * 这是 *DiscoverFragment* 自己的菜单点击处理器。
     */
    private boolean handleMenuClick(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_scan) { //
            Toast.makeText(getContext(), "发现页 扫一扫", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.menu_add_friend) { //
            Toast.makeText(getContext(), "发现页 添加好友", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.menu_create_group) { //
            Toast.makeText(getContext(), "发现页 发起群聊", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    // 6. [新] 防止内存泄漏
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}