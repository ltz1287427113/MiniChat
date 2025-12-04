package com.example.minichat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.minichat.activity.NewFriendsActivity;
import com.example.minichat.activity.FriendProfileActivity;
import com.example.minichat.adapter.ContactsAdapter;
import com.example.minichat.databinding.FragmentContactsBinding;
import com.example.minichat.model.ContactItem;
import com.example.minichat.model.FunctionItem;
import com.example.minichat.viewmodel.ContactsViewModel;

import java.util.ArrayList;

/**
 * [检查版] ContactsFragment
 * 确保正确传递username到FriendProfileActivity
 */
public class ContactsFragment extends Fragment implements ContactsAdapter.OnItemClickListener {

    private static final String TAG = "ContactsFragment";
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

        adapter = new ContactsAdapter(new ArrayList<>(), this);
        binding.rvContactsList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvContactsList.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ContactsViewModel.class);

        viewModel.getContactList().observe(getViewLifecycleOwner(), items -> {
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

            // [关键] 打印日志检查数据
            Log.d(TAG, "点击好友: username=" + contactItem.getUsername() + ", displayName=" + contactItem.getDisplayName() + ", userId=" + contactItem.getFriendUserid());

            Intent intent = new Intent(getActivity(), FriendProfileActivity.class);
            intent.putExtra("FRIEND_ID", contactItem.getFriendUserid());
            intent.putExtra("FRIEND_USERNAME", contactItem.getUsername()); // [确保传递]

            Log.d(TAG, "启动FriendProfileActivity，传递参数: FRIEND_USERNAME=" + contactItem.getUsername());

            startActivity(intent);
        }
    }

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