package com.example.minichat.fragment; // (确保这是你的包名)

import android.content.Intent;
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
import com.example.minichat.activity.NewFriendsActivity;
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

    }


    /**
     * [注释]
     * 设置 RecyclerView (保持不变)
     */
    private void setupRecyclerView() {
        // ... (此方法完全不变) ...
        List<Object> items = buildSortedList();
        adapter = new ContactsAdapter(items, item -> {
            if (item instanceof FunctionItem) {
                FunctionItem functionItem = (FunctionItem) item;
                if ("新的朋友".equals(functionItem.getName())) {
                    Intent intent = new Intent(requireActivity(), NewFriendsActivity.class);
                    startActivity(intent);
                } else if ("群聊".equals(functionItem.getName())) {
                    Toast.makeText(requireContext(), "点击了群聊", Toast.LENGTH_SHORT).show();
                }
            }
        });
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