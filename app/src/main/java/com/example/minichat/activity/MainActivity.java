package com.example.minichat.activity; // (确保这是你的包名)

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import androidx.appcompat.widget.Toolbar; // [新导入]

import com.example.minichat.R;
import com.example.minichat.adapter.MainViewPagerAdapter;
import com.example.minichat.databinding.ActivityMainBinding;
import com.example.minichat.utils.MenuHelper; // [新导入]
import com.google.android.material.navigation.NavigationBarView;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // [注释] (我们不再需要 applyWindowInsets 了,
        //  因为 AppBarLayout 里的 fitsSystemWindows=true 会自动处理)

        // 4. 初始化 ViewPager2 (不变)
        adapter = new MainViewPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        // --- 核心：实现“联动” ---

        // 5. [修改] 监听 ViewPager2 的滑动
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        // "聊天"
                        binding.appBarLayout.setVisibility(View.VISIBLE);
                        binding.topNav.setTitle("微信");
                        binding.bottomNavView.setSelectedItemId(R.id.nav_chat);
                        break;
                    case 1:
                        // "通讯录"
                        binding.appBarLayout.setVisibility(View.VISIBLE);
                        binding.topNav.setTitle("通讯录");
                        binding.bottomNavView.setSelectedItemId(R.id.nav_contacts);
                        break;
                    case 2:
                        // "发现"
                        binding.appBarLayout.setVisibility(View.VISIBLE);
                        binding.topNav.setTitle("发现");
                        binding.bottomNavView.setSelectedItemId(R.id.nav_discover);
                        break;
                    case 3:
                        // "我"
                        binding.appBarLayout.setVisibility(View.GONE); // [核心] 隐藏 Toolbar
                        binding.bottomNavView.setSelectedItemId(R.id.nav_me);
                        break;
                }
            }
        });

        // 6. [修改] 监听底部导航栏的点击
        binding.bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_chat) {
                    binding.viewPager.setCurrentItem(0, true);
                } else if (itemId == R.id.nav_contacts) {
                    binding.viewPager.setCurrentItem(1, true);
                } else if (itemId == R.id.nav_discover) {
                    binding.viewPager.setCurrentItem(2, true);
                } else if (itemId == R.id.nav_me) {
                    binding.viewPager.setCurrentItem(3, true);
                }
                return true;
            }
        });

        // 7. [新] 将所有 Toolbar 菜单逻辑移到这里
        setupToolbarMenu();
    }

    /**
     * [注释]
     * [新方法]
     * 设置 Toolbar 上的菜单点击事件
     */
    private void setupToolbarMenu() {
        binding.topNav.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_search) {
                    Toast.makeText(MainActivity.this, "点击了 搜索", Toast.LENGTH_SHORT).show();
                    return true;

                } else if (itemId == R.id.menu_more) {
                    // [关键] 当“更多”按钮被点击时...
                    View anchorView = binding.topNav.findViewById(R.id.menu_more);

                    // [新代码] 调用我们的可复用工具类 MenuHelper
                    MenuHelper.showPopupMenuWithIcons(MainActivity.this,       // 1. Context
                            anchorView,              // 2. 锚点
                            R.menu.top_nav_menu,     // 3. 加载菜单
                            MainActivity.this::handleMenuClick    // 4. [新] 点击的回调方法
                    );
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * [注释]
     * [新方法]
     * 这是 MainActivity 自己的菜单点击处理器。
     * (从 ChatFragment 移动到这里)
     */
    private boolean handleMenuClick(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_scan) { //
            Toast.makeText(this, "点击了 扫一扫", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.menu_add_friend) { //
            Toast.makeText(this, "点击了 添加好友", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.menu_create_group) { //
            Toast.makeText(this, "点击了 发起群聊", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }
}