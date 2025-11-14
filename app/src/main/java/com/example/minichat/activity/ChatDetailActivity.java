package com.example.minichat.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.minichat.adapter.MessageAdapter; // [导入]
import com.example.minichat.data.local.MessageEntity; // [导入]
import com.example.minichat.databinding.ActivityChatDetailBinding; // [导入]
import com.example.minichat.viewmodel.ChatViewModel; // [导入]

import java.util.List;

public class ChatDetailActivity extends AppCompatActivity {

    // 1. ViewBinding
    private ActivityChatDetailBinding binding;

    // 2. ViewModel (我们早就创建好了)
    private ChatViewModel viewModel;

    // 3. Adapter (我们刚创建的)
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 4. [核心] ViewBinding 在 Activity 中的标准写法
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 5. [核心] 初始化 ViewModel
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        // 6. [新功能] 获取 ChatFragment 传来的名字并设置标题
        String chatName = getIntent().getStringExtra("CHAT_NAME");
        if (chatName != null) {
            binding.toolbar.setTitle(chatName);
        }
        // (设置返回按钮)
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // 7. 初始化 RecyclerView
        setupRecyclerView();

        // 8. [核心] 观察(observe) ViewModel 中的 LiveData
        viewModel.getMessages().observe(this, new Observer<List<MessageEntity>>() {
            @Override
            public void onChanged(List<MessageEntity> messages) {
                // 当数据库数据变化时，这个方法会被自动调用
                messageAdapter.setMessages(messages);
                // 滚动到底部
                if (messages != null && !messages.isEmpty()) {
                    binding.rvMessageList.scrollToPosition(messages.size() - 1);
                }
            }
        });

        // 9. 设置发送按钮的点击事件
        binding.btnSend.setOnClickListener(v -> {
            String text = binding.etMessageInput.getText().toString();
            if (!text.isEmpty()) {
                // 10. [核心] 通知 ViewModel 发送消息
                viewModel.sendMessage(text);
                binding.etMessageInput.setText("");
            }
        });

        // (可选) 添加一个测试按钮来模拟接收消息
        binding.etMessageInput.setOnLongClickListener(v -> {
            viewModel.simulateReceiveMessage("这是一条模拟的对方消息！");
            return true;
        });
    }

    private void setupRecyclerView() {
        messageAdapter = new MessageAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // 让列表从底部开始显示
        binding.rvMessageList.setLayoutManager(layoutManager);
        binding.rvMessageList.setAdapter(messageAdapter);
    }
}