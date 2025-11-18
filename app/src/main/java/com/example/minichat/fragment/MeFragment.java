package com.example.minichat.fragment; // (确保这是你的包名)

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast; // [新导入]

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.minichat.databinding.FragmentMeBinding; // [新导入]

/**
 * [注释]
 * 这是“我”标签页的 Fragment。
 * 它负责加载 fragment_me.xml 并处理点击事件。
 */
public class MeFragment extends Fragment {

    // 1. [新] ViewBinding
    private FragmentMeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // 2. [修改] 使用 ViewBinding 加载布局
        binding = FragmentMeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 3. [新] 为“个人信息”区域 添加点击事件
        binding.profileBlock.setOnClickListener(v -> {
            Toast.makeText(getContext(), "点击了 个人信息", Toast.LENGTH_SHORT).show();
            // TODO: 在这里跳转到个人信息详情页
        });

        // 4. [新] 为“设置”按钮 添加点击事件
        binding.settingsBlock.setOnClickListener(v -> {
            Toast.makeText(getContext(), "点击了 设置", Toast.LENGTH_SHORT).show();
            // TODO: 在这里跳转到设置页
        });
    }

    // 5. [新] 防止内存泄漏
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}