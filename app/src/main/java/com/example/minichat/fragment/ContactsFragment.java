package com.example.minichat.fragment; // (确保这是你的包名)

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem; // [新导入]
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.minichat.R;
import com.example.minichat.adapter.ContactsAdapter;
import com.example.minichat.databinding.FragmentContactsBinding;
import com.example.minichat.model.ContactItem;
import com.example.minichat.model.FunctionItem;
import com.example.minichat.model.HeaderItem;
import com.example.minichat.utils.MenuHelper; // [新导入]

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ContactsFragment extends Fragment {

    private FragmentContactsBinding binding;
    private ContactsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 4. 设置 RecyclerView
        setupRecyclerView();

        // 5. [核心修改]
        // 设置 Toolbar 上的点击事件
        binding.topNav.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_search) {
                Toast.makeText(getContext(), "点击了 搜索", Toast.LENGTH_SHORT).show();
                return true;

            } else if (itemId == R.id.menu_more) {
                // [关键] 当“更多”按钮被点击时...
                View anchorView = binding.topNav.findViewById(R.id.menu_more);

                // [新代码] 我们也调用 MenuHelper
                // 注意：它加载 *相同* 的 R.menu.top_nav_menu
                // 但它使用 *不同* 的回调方法
                MenuHelper.showPopupMenuWithIcons(requireActivity(), anchorView, R.menu.top_nav_menu, this::handleMenuClick // [新]
                );
                return true;
            }
            return false;
        });
    }

    /**
     * [注释]
     * [新方法]
     * 这是 *ContactsFragment* 自己的菜单点击处理器。
     * （它和 ChatFragment 的处理器可以做不同的事）
     */
    private boolean handleMenuClick(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_scan) { //
            Toast.makeText(getContext(), "通讯录 扫一扫", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.menu_add_friend) { //
            Toast.makeText(getContext(), "通讯录 添加好友", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.menu_create_group) { //
            Toast.makeText(getContext(), "通讯录 发起群聊", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    /**
     * [注释]
     * 设置 RecyclerView (保持不变)
     */
    private void setupRecyclerView() {
        // ... (此方法完全不变) ...
        List<Object> items = buildSortedList();
        adapter = new ContactsAdapter(items);
        binding.rvContactsList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvContactsList.setAdapter(adapter);
        binding.rvContactsList.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
    }

    /**
     * [注释]
     * 创建 A-Z 排序列表 (保持不变)
     */
    private List<Object> buildSortedList() {
        // ... (此方法完全不变) ...
        List<ContactItem> contacts = new ArrayList<>();
        contacts.add(new ContactItem("user_aaa_1", "AAA老哥助理-小橙学姐"));
        contacts.add(new ContactItem("user_aaa_2", "AAA全能工作室"));
        contacts.add(new ContactItem("user_b_1", "A潮艺造型"));
        contacts.add(new ContactItem("user_c_1", "阿蛋（影视会员）"));
        contacts.add(new ContactItem("user_d_1", "B潮艺造型"));
        contacts.add(new ContactItem("user_e_1", "C阿蛋（影视会员）"));
        contacts.add(new ContactItem("user_f_1", "D阿蛋（影视会员）"));

        Collections.sort(contacts, new Comparator<ContactItem>() {
            @Override
            public int compare(ContactItem o1, ContactItem o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        List<Object> items = new ArrayList<>();
        items.add(new FunctionItem("新的朋友", R.drawable.ic_addfriend));
        items.add(new FunctionItem("群聊", R.drawable.ic_group_chat));

        String lastHeader = "";
        for (ContactItem contact : contacts) {
            String currentHeader = contact.getName().substring(0, 1).toUpperCase();
            if (currentHeader.matches("[A-Z]")) {
                if (!currentHeader.equals(lastHeader)) {
                    items.add(new HeaderItem(currentHeader));
                    lastHeader = currentHeader;
                }
            }
            items.add(contact);
        }
        return items;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}