package com.example.minichat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.minichat.R;
import com.example.minichat.adapter.MessageAdapter;
import com.example.minichat.data.local.MessageEntity;
import com.example.minichat.data.model.enum_.ChatMessageTypeEnum;
import com.example.minichat.data.model.response.ChatMessage;
import com.example.minichat.databinding.ActivityChatDetailBinding;
import com.example.minichat.utils.SpUtils;
import com.example.minichat.utils.WebSocketManager;
import com.example.minichat.viewmodel.ChatViewModel;
import com.example.minichat.viewmodel.FriendProfileViewModel;

/**
 * [优化版] ChatDetailActivity
 * 改进：从好友详情获取正确的备注名称
 */
public class ChatDetailActivity extends AppCompatActivity implements WebSocketManager.OnMessageReceivedListener {

    private static final String TAG = "ChatDetailActivity";
    private ActivityChatDetailBinding binding;
    private ChatViewModel chatViewModel;
    private FriendProfileViewModel friendViewModel; // [新增]
    private MessageAdapter messageAdapter;
    private SharedPreferences prefs;
    private final String KEYBOARD_HEIGHT_KEY = "keyboard_height";
    private int myUserId;
    private int targetId;
    private String targetUsername; // [新增]
    private boolean isGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        myUserId = SpUtils.getUser(this).getUserId();

        // 获取Intent数据
        targetId = getIntent().getIntExtra("FRIEND_ID", -1);
        targetUsername = getIntent().getStringExtra("CHAT_USERNAME"); // [新增]
        isGroup = false;

        String chatName = getIntent().getStringExtra("CHAT_NAME");

        Log.d(TAG, "当前用户ID: " + myUserId);
        Log.d(TAG, "目标用户ID: " + targetId);
        Log.d(TAG, "目标用户名: " + targetUsername);

        if (targetId == -1) {
            finish();
            return;
        }

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        friendViewModel = new ViewModelProvider(this).get(FriendProfileViewModel.class);

        WebSocketManager.getInstance().addListener(this);

        setupRecyclerView();
        setupInputControls();
        setupKeyboardAndPanelListener();
        observeChatData();

        // [核心改进] 如果有username，获取最新的好友信息（包括备注）
        if (targetUsername != null && !targetUsername.isEmpty()) {
            loadFriendInfo(targetUsername, chatName);
        } else {
            setupToolbar(chatName);
        }
    }

    /**
     * [新增] 加载好友信息，获取正确的备注名称
     */
    private void loadFriendInfo(String username, String fallbackName) {
        friendViewModel.getFriendDetailResult().observe(this, result -> {
            if (result.error != null) {
                Log.e(TAG, "加载好友信息失败: " + result.error.getMessage());
                setupToolbar(fallbackName); // 使用传入的名字作为后备
            } else if (result.data != null) {
                // [核心] 使用 getDisplayName() 获取备注或昵称
                String displayName = result.data.getDisplayName();
                Log.d(TAG, "加载到好友备注: " + displayName);
                setupToolbar(displayName);
            }
        });

        friendViewModel.loadFriendDetail(username);
    }

    private void setupToolbar(String chatName) {
        if (chatName != null && !chatName.isEmpty()) {
            binding.toolbar.setTitle(chatName);
            Log.d(TAG, "设置标题: " + chatName);
        }

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_chat_settings) {
                Intent intent = new Intent(this, com.example.minichat.activity.ChatInfoActivity.class);

                // [修复] 使用标准的Intent参数传递
                intent.putExtra(com.example.minichat.activity.ChatInfoActivity.EXTRA_FRIEND_ID, targetId);
                intent.putExtra(com.example.minichat.activity.ChatInfoActivity.EXTRA_FRIEND_USERNAME, targetUsername);

                Log.d(TAG, "打开ChatInfoActivity: friendId=" + targetId + ", friendUsername=" + targetUsername);

                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void observeChatData() {
        chatViewModel.getChatHistory(myUserId, targetId, isGroup).observe(this, messages -> {
            Log.d(TAG, "收到消息列表，数量: " + (messages != null ? messages.size() : 0));

            messageAdapter.setMessages(messages);
            if (messages != null && !messages.isEmpty()) {
                binding.rvMessageList.scrollToPosition(messages.size() - 1);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupRecyclerView() {
        messageAdapter = new MessageAdapter(myUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.rvMessageList.setLayoutManager(layoutManager);
        binding.rvMessageList.setAdapter(messageAdapter);

        binding.rvMessageList.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyboard();
                if (binding.plusMenuPanel.getVisibility() == View.VISIBLE) {
                    binding.plusMenuPanel.setVisibility(View.GONE);
                }
            }
            return false;
        });
    }

    private void setupInputControls() {
        binding.etMessageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    binding.btnSend.setVisibility(View.GONE);
                    binding.btnMore.setVisibility(View.VISIBLE);
                } else {
                    binding.btnSend.setVisibility(View.VISIBLE);
                    binding.btnMore.setVisibility(View.GONE);
                }
            }
        });

        binding.btnSend.setOnClickListener(v -> {
            String text = binding.etMessageInput.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(text);
                binding.etMessageInput.setText("");
            }
        });

        binding.btnMore.setOnClickListener(v -> handlePlusButtonClick());
    }

    private void sendMessage(String text) {
        ChatMessage msg = new ChatMessage(targetId, null, ChatMessageTypeEnum.PRIVATE_MESSAGE, text);

        msg.setSenderId(myUserId);

        Log.d(TAG, "发送消息: " + text + ", targetId=" + targetId);

        WebSocketManager.getInstance().sendMessage(msg);
        chatViewModel.saveMessageToLocal(msg);
    }

    private void setupKeyboardAndPanelListener() {
        int lastKeyboardHeight = prefs.getInt(KEYBOARD_HEIGHT_KEY, 0);
        if (lastKeyboardHeight > 0) {
            setPanelHeight(lastKeyboardHeight);
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            boolean isImeVisible = insets.isVisible(WindowInsetsCompat.Type.ime());
            int imeHeight = imeInsets.bottom;

            ViewGroup.MarginLayoutParams toolbarParams = (ViewGroup.MarginLayoutParams) binding.toolbar.getLayoutParams();
            toolbarParams.topMargin = systemBarInsets.top;
            binding.toolbar.setLayoutParams(toolbarParams);

            if (isImeVisible && imeHeight > 0) {
                prefs.edit().putInt(KEYBOARD_HEIGHT_KEY, imeHeight).apply();
                setPanelHeight(imeHeight);
                if (binding.plusMenuPanel.getVisibility() == View.VISIBLE) {
                    binding.plusMenuPanel.setVisibility(View.GONE);
                }
            }

            int bottomPadding = isImeVisible ? imeHeight : systemBarInsets.bottom;
            v.setPadding(0, 0, 0, bottomPadding);

            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void setPanelHeight(int height) {
        ViewGroup.LayoutParams params = binding.plusMenuPanel.getLayoutParams();
        if (params.height != height) {
            params.height = height;
            binding.plusMenuPanel.setLayoutParams(params);
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void handlePlusButtonClick() {
        int lastKeyboardHeight = prefs.getInt(KEYBOARD_HEIGHT_KEY, 0);
        int defaultPanelHeight = (int) (250 * getResources().getDisplayMetrics().density);
        int panelHeight = (lastKeyboardHeight > 0) ? lastKeyboardHeight : defaultPanelHeight;
        setPanelHeight(panelHeight);

        hideKeyboard();

        if (binding.plusMenuPanel.getVisibility() == View.VISIBLE) {
            binding.plusMenuPanel.setVisibility(View.GONE);
        } else {
            binding.plusMenuPanel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebSocketManager.getInstance().removeListener(this);
    }

    @Override
    public void onMessageReceived(ChatMessage message) {
        Log.d(TAG, "收到WebSocket消息: " + message.getContent());

        runOnUiThread(() -> {
            chatViewModel.saveMessageToLocal(message);
        });
    }
}