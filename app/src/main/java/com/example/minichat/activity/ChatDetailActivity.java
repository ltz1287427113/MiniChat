package com.example.minichat.activity;

import static java.lang.Integer.parseInt;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat; // [新导入]
import androidx.core.view.WindowInsetsCompat; // [新导入]
import androidx.core.graphics.Insets; // [新导入]
import androidx.lifecycle.Observer;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ChatDetailActivity extends AppCompatActivity implements WebSocketManager.OnMessageReceivedListener{

    private static final Logger log = LoggerFactory.getLogger(ChatDetailActivity.class);
    private ActivityChatDetailBinding binding;
    private ChatViewModel viewModel;
    private MessageAdapter messageAdapter;
    private SharedPreferences prefs;
    private final String KEYBOARD_HEIGHT_KEY = "keyboard_height";
    private int myUserId;
    private int targetId; // 对方ID (私聊) 或 群ID (群聊)
    private boolean isGroup; // 是否是群聊

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // [新] 1. 开启 Edge-to-Edge (替换掉 fitsSystemWindows="true")
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int myIdStr = SpUtils.getUser(this).getUserId();
        try {
            myUserId = parseInt(String.valueOf(myIdStr));
        } catch (NumberFormatException e) {
            myUserId = 0; // 异常处理
        }

        // TODO 判断是否是群聊
        targetId = getIntent().getIntExtra("FRIEND_ID", -1);
        isGroup = false; // 暂时默认私聊

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        // 2. [核心] 告诉 Manager：“我要听消息”
        WebSocketManager.getInstance().addListener(this);

        setupToolbar();
        setupRecyclerView();
        observeViewModel();
        setupInputControls();
        setupMessageList();

        // 3. [核心] 观察数据库数据
        observeChatData();
        // [新] 2. 设置键盘和面板的监听
        setupKeyboardAndPanelListener();
    }

    private void observeChatData() {
        // 只加载“我和他”的聊天记录
        viewModel.getChatHistory(myUserId, targetId, isGroup).observe(this, messages -> {
            // 数据库变动 -> 自动更新 UI
            messageAdapter.setMessages(messages);
            if (messages != null && !messages.isEmpty()) {
                binding.rvMessageList.scrollToPosition(messages.size() - 1);
            }
        });
    }
    private void setupMessageList() {
        messageAdapter = new MessageAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.rvMessageList.setLayoutManager(layoutManager);
        binding.rvMessageList.setAdapter(messageAdapter);
    }
    private void setupToolbar() {
        String chatName = getIntent().getStringExtra("CHAT_NAME");
        String chatUserName = getIntent().getStringExtra("CHAT_USERNAME");
        if (chatName != null) {
            binding.toolbar.setTitle(chatName);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_chat_settings) {
                Toast.makeText(this, "点击了聊天设置", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        binding.toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_chat_settings) {
                // [修改] 跳转到聊天信息页面
                Intent intent = new Intent(this, ChatInfoActivity.class);
                // 把当前聊天的名字传过去，方便显示
                intent.putExtra("CHAT_NAME", chatName);
                intent.putExtra("CHAT_USERNAME", chatUserName);
                // 传递 CHAT_ID，用于清空指定记录和删除好友
                intent.putExtra("CHAT_ID", getIntent().getStringExtra("CHAT_ID"));
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void observeViewModel() {
        viewModel.getRecentSessionList(3).observe(this, new Observer<List<MessageEntity>>() {
            @Override
            public void onChanged(List<MessageEntity> messages) {
                messageAdapter.setMessages(messages);
                if (messages != null && !messages.isEmpty()) {
                    binding.rvMessageList.scrollToPosition(messages.size() - 1);
                }
            }
        });
    }

    private void setupInputControls() {
        binding.etMessageInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
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
            String text = binding.etMessageInput.getText().toString();
            if (!text.isEmpty()) {
                // A. 创建消息对象 (用于发送)
                ChatMessage msg = new ChatMessage(
                        targetId, // 接收者
                        null,     // groupId
                        ChatMessageTypeEnum.PRIVATE_MESSAGE,
                        text
                );

                // B. WebSocket 发送
                WebSocketManager.getInstance().sendMessage(msg);

                // C. [关键] 也要存入本地数据库，这样 UI 才会立即显示“我发的消息”
                // 我们需要手动构造一个完整的 ChatMessage (补全 senderId) 存入本地
                msg.setSenderId(myUserId);
                viewModel.saveMessageToLocal(msg);

                binding.etMessageInput.setText("");
            }
        });

        binding.btnMore.setOnClickListener(v -> handlePlusButtonClick());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupRecyclerView() {
        messageAdapter = new MessageAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.rvMessageList.setLayoutManager(layoutManager);
        binding.rvMessageList.setAdapter(messageAdapter);

        binding.rvMessageList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideKeyboard();
                    if (binding.plusMenuPanel.getVisibility() == View.VISIBLE) {
                        binding.plusMenuPanel.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });
    }

    // [新方法] 监听键盘，动态设置高度
    private void setupKeyboardAndPanelListener() {
        // 1. 从“本地存储”读取上次保存的键盘高度
        int lastKeyboardHeight = prefs.getInt(KEYBOARD_HEIGHT_KEY, 0);
        if (lastKeyboardHeight > 0) {
            setPanelHeight(lastKeyboardHeight);
        }

        // 3. 在“根视图”上添加一个监听器
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {

            // 4. [修复] 获取系统栏(状态栏)和键盘(IME)的 Insets
            Insets systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());

            // 5. [修复] 检查键盘是否可见
            boolean isImeVisible = insets.isVisible(WindowInsetsCompat.Type.ime());
            int imeHeight = imeInsets.bottom;

            // 6. [修复] 手动处理 Toolbar 的顶部边距
            ViewGroup.MarginLayoutParams toolbarParams = (ViewGroup.MarginLayoutParams) binding.toolbar.getLayoutParams();
            toolbarParams.topMargin = systemBarInsets.top;
            binding.toolbar.setLayoutParams(toolbarParams);

            if (isImeVisible) {
                // 键盘弹出了
                if (imeHeight > 0) {
                    // 7. [修复] 测量到了高度！保存并应用
                    prefs.edit().putInt(KEYBOARD_HEIGHT_KEY, imeHeight).apply();
                    setPanelHeight(imeHeight);
                }

                // 8. [核心修复]
                // 键盘弹出时，强制隐藏功能面板
                // 这就实现了“键盘覆盖菜单”
                if (binding.plusMenuPanel.getVisibility() == View.VISIBLE) {
                    binding.plusMenuPanel.setVisibility(View.GONE);
                }
            }

            // 9. [核心修复]
            // 将键盘的高度(imeHeight)或系统导航栏的高度(systemBarInsets.bottom)
            // 作为 *底部内边距* 应用到 *根视图*
            // 这会把 input_layout 和 plus_menu_panel 一起“推”上来
            int bottomPadding = isImeVisible ? imeHeight : systemBarInsets.bottom;
            v.setPadding(0, 0, 0, bottomPadding);

            return WindowInsetsCompat.CONSUMED; // 我们已经处理了所有 Insets
        });
    }

    // [新方法] 统一设置面板高度
    private void setPanelHeight(int height) {
        ViewGroup.LayoutParams params = binding.plusMenuPanel.getLayoutParams();
        if (params.height != height) {
            params.height = height;
            binding.plusMenuPanel.setLayoutParams(params);
        }
    }

    // [新方法] 隐藏软键盘
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // [修改] 处理“更多”按钮点击
    private void handlePlusButtonClick() {
        // 1. 在显示面板 *之前*，确保它有正确的高度
        int lastKeyboardHeight = prefs.getInt(KEYBOARD_HEIGHT_KEY, 0);
        int defaultPanelHeight = (int) (250 * getResources().getDisplayMetrics().density); // 默认 250dp
        int panelHeight = (lastKeyboardHeight > 0) ? lastKeyboardHeight : defaultPanelHeight;
        setPanelHeight(panelHeight);

        // 2. 隐藏键盘 (保持不变)
        hideKeyboard();

        // 3. 切换面板可见性 (保持不变)
        if (binding.plusMenuPanel.getVisibility() == View.VISIBLE) {
            binding.plusMenuPanel.setVisibility(View.GONE);
        } else {
            binding.plusMenuPanel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 3. [核心] 页面销毁时，告诉 Manager：“我不听了” (防止内存泄漏)
        WebSocketManager.getInstance().removeListener(this);
    }
    // 4. [核心] 实现接口方法：收到消息时会跑这里
    @Override
    public void onMessageReceived(ChatMessage message) {
        // 在主线程更新 UI
        runOnUiThread(() -> {
            // 存入数据库 -> LiveData 自动更新 UI
            viewModel.saveMessageToLocal(message);
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}