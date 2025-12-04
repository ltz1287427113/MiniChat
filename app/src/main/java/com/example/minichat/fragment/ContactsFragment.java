package com.example.minichat.fragment; // (确保这是你的包名)

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem; // [新导入]
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.minichat.R;
import com.example.minichat.activity.NewFriendsActivity;
import com.example.minichat.activity.FriendProfileActivity;
import com.example.minichat.adapter.ContactsAdapter;
import com.example.minichat.databinding.FragmentContactsBinding;
import com.example.minichat.model.ContactItem;
import com.example.minichat.model.FunctionItem;
import com.example.minichat.model.HeaderItem;
import com.example.minichat.utils.MenuHelper; // [新导入]
import com.example.minichat.viewmodel.ContactsViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ContactsFragment extends Fragment implements ContactsAdapter.OnItemClickListener {

    private FragmentContactsBinding binding;
    private ContactsAdapter adapter;
    private ContactsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. 初始化 RecyclerView (先给个空列表，防止白屏)
        adapter = new ContactsAdapter(new ArrayList<>(), this);
        binding.rvContactsList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvContactsList.setAdapter(adapter);
        // binding.rvContactsList.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));

        // ... Toolbar 设置代码不变 ...

        // 2. [核心] 初始化 ViewModel 并观察数据
        viewModel = new ViewModelProvider(this).get(ContactsViewModel.class);

        viewModel.getContactList().observe(getViewLifecycleOwner(), items -> {
            // 数据回来了！更新列表
            adapter.updateList(items);
        });

        viewModel.getErrorMsg().observe(getViewLifecycleOwner(), msg -> {
            Toast.makeText(getContext(), "加载失败: " + msg, Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public void onItemClick(Object item) {
        if (item instanceof FunctionItem) {
            FunctionItem functionItem = (FunctionItem) item;
            if ("新的朋友".equals(functionItem.getName())) {
                Intent intent = new Intent(getActivity(), NewFriendsActivity.class);
                startActivity(intent);
            } else if ("群聊".equals(functionItem.getName())) {
                Toast.makeText(getContext(), "点击了群聊", Toast.LENGTH_SHORT).show();
            }
        } else if (item instanceof ContactItem) {
            ContactItem contactItem = (ContactItem) item;
            Log.d("ContactsFragment", "Clicked ContactItem with username: " + contactItem.getUsername());
            Intent intent = new Intent(getActivity(), FriendProfileActivity.class);
            intent.putExtra("FRIEND_ID", contactItem.getFriendUserid());
            intent.putExtra("FRIEND_USERNAME", contactItem.getUsername());
            startActivity(intent);
        }
    }


    // [修改] 放在 onResume 里，每次切回来都刷新一下，保证好友列表最新
    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadContacts();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}