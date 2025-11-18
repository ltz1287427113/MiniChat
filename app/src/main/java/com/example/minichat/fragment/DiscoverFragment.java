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
    }

    // 6. [新] 防止内存泄漏
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}