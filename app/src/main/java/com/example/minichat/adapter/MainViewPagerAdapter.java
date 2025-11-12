package com.example.minichat.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.minichat.fragment.ChatFragment;
import com.example.minichat.fragment.ContactsFragment;
import com.example.minichat.fragment.DiscoverFragment;
import com.example.minichat.fragment.MeFragment;

public class MainViewPagerAdapter extends FragmentStateAdapter {

    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * [核心] 根据位置返回对应的 Fragment
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ChatFragment();
            case 1:
                return new ContactsFragment();
            case 2:
                return new DiscoverFragment();
            case 3:
                return new MeFragment();
            default:
                return new ChatFragment(); // 默认
        }
    }

    /**
     * [核心] 返回 Fragment 的总数
     */
    @Override
    public int getItemCount() {
        return 4; // 因为我们有 4 个菜单项
    }
}