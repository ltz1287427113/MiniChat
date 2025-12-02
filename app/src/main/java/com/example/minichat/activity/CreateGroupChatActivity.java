package com.example.minichat.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.minichat.adapter.SelectContactAdapter;
import com.example.minichat.databinding.ActivityCreateGroupChatBinding;
import com.example.minichat.viewmodel.ContactsViewModel;

import java.util.Set;

public class CreateGroupChatActivity extends AppCompatActivity {

    private ActivityCreateGroupChatBinding binding;
    private ContactsViewModel viewModel;
    private SelectContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        initData();
    }

    private void initView() {
        // Toolbar
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Adapter
        adapter = new SelectContactAdapter(count -> {
            updateCreateButton(count);
        });

        // RecyclerView
        binding.rvContactSelect.setLayoutManager(new LinearLayoutManager(this));
        binding.rvContactSelect.setAdapter(adapter);

        // Button logic
        binding.btnCreate.setOnClickListener(v -> {
            Set<String> selected = adapter.getSelectedUsernames();
            if (selected.isEmpty()) return;

            // TODO: Implement actual group creation logic
            Toast.makeText(this, "选中了 " + selected.size() + " 人", Toast.LENGTH_SHORT).show();
            Log.d("CreateGroupChat", "Selected users: " + selected);
        });
    }

    private void initData() {
        viewModel = new ViewModelProvider(this).get(ContactsViewModel.class);

        viewModel.getContactList().observe(this, items -> {
            if (items != null) {
                adapter.updateList(items);
            }
        });

        viewModel.getErrorMsg().observe(this, msg -> {
            Toast.makeText(this, "加载失败: " + msg, Toast.LENGTH_SHORT).show();
        });

        // Load contacts without function items (New Friends, Group Chat)
        viewModel.loadContacts(false);
    }

    private void updateCreateButton(int count) {
        binding.btnCreate.setText("完成(" + count + ")");
        binding.btnCreate.setEnabled(count > 0);
    }
}
