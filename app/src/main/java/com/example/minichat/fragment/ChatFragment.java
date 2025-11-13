package com.example.minichat.fragment; // (确保这是你的包名)

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem; // [导入]
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu; // [导入]
import android.widget.Toast; // [导入]

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.minichat.R;
import com.example.minichat.databinding.FragmentChatBinding; // [导入]

import java.lang.reflect.Method;

public class ChatFragment extends Fragment {

    // 1. 添加 ViewBinding 引用
    private FragmentChatBinding binding;

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

        // 3. [核心] 找到你的“顶部导航栏”布局，并从它内部找到“菜单按钮”
        // (我们必须用 findViewById，因为图标在 LinearLayout 内部)
        binding.topNav.findViewById(R.id.iv_top_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 4. 当点击时，调用我们下面的方法
                showTopPopupMenu(v);
            }
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