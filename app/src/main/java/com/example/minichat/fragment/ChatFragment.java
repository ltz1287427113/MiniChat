package com.example.minichat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.minichat.activity.ChatDetailActivity;
import com.example.minichat.adapter.SessionAdapter;
import com.example.minichat.data.local.MessageEntity;
import com.example.minichat.databinding.FragmentChatBinding;
import com.example.minichat.model.Session;
import com.example.minichat.utils.SpUtils;
import com.example.minichat.viewmodel.ChatViewModel;
import com.example.minichat.viewmodel.ContactsViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [修复版] 聊天列表Fragment
 * 核心改进：从好友列表获取备注名称
 */
public class ChatFragment extends Fragment {

    private static final String TAG = "ChatFragment";
    private FragmentChatBinding binding;
    private SessionAdapter sessionAdapter;
    private ChatViewModel chatViewModel;
    private ContactsViewModel contactsViewModel; // [新增]
    private int myUserId;
    private String targetUsername;

    // [新增] 用于缓存好友信息 (username -> 显示名称)
    private Map<String, FriendInfo> friendInfoMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myUserId = SpUtils.getUser(getContext()).getUserId();
        Log.d(TAG, "当前用户ID: " + myUserId);

        setupRecyclerView();

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        contactsViewModel = new ViewModelProvider(this).get(ContactsViewModel.class);

        // [新增] 先加载好友列表，构建缓存
        loadFriendsInfo();
    }

    /**
     * [核心新增] 加载好友列表，构建本地缓存
     */
    private void loadFriendsInfo() {
        contactsViewModel.getContactList().observe(getViewLifecycleOwner(), items -> {
            if (items == null) return;

            // 清空旧缓存
            friendInfoMap.clear();

            // 遍历好友列表，构建缓存
            for (Object item : items) {
                if (item instanceof com.example.minichat.model.ContactItem) {
                    com.example.minichat.model.ContactItem contact = (com.example.minichat.model.ContactItem) item;

                    FriendInfo info = new FriendInfo();
                    info.displayName = contact.getDisplayName(); // 这是备注或昵称
                    info.avatarUrl = contact.getAvatarUrl();
                    info.userId = contact.getFriendUserid();

                    // 使用username作为key（因为username是唯一的）
                    friendInfoMap.put(contact.getUsername(), info);
                    targetUsername = contact.getUsername(); // 保存当前聊天对象
                    Log.d(TAG, "缓存好友信息: username=" + contact.getUsername() + ", displayName=" + info.displayName + ", userId=" + info.userId);
                }
            }

            Log.d(TAG, "好友信息缓存完成，共 " + friendInfoMap.size() + " 个好友");

            // 好友列表加载完成后，再加载会话列表
            loadSessions();
        });

        // 触发加载好友列表（不需要功能项）
        contactsViewModel.loadContacts(false);
    }

    /**
     * [新增] 加载会话列表
     */
    private void loadSessions() {
        chatViewModel.getRecentSessionList(myUserId).observe(getViewLifecycleOwner(), messageEntities -> {
            Log.d(TAG, "收到会话列表，数量: " + (messageEntities != null ? messageEntities.size() : 0));

            List<Session> sessions = convertToSessions(messageEntities);
            Log.d(TAG, "转换后的会话数: " + sessions.size());

            sessionAdapter.updateSessions(sessions);
        });
    }

    private void setupRecyclerView() {
        binding.rvSessionList.setLayoutManager(new LinearLayoutManager(getContext()));
        sessionAdapter = new SessionAdapter(new ArrayList<>());
        binding.rvSessionList.setAdapter(sessionAdapter);

        sessionAdapter.setOnSessionClickListener(session -> {
            Log.d(TAG, "点击会话: " + session.getName() + ", ID=" + session.getId());

            Intent intent = new Intent(getActivity(), ChatDetailActivity.class);

            try {
                int friendId = Integer.parseInt(session.getId());
                intent.putExtra("FRIEND_ID", friendId);
            } catch (NumberFormatException e) {
                Log.e(TAG, "会话ID转换失败: " + session.getId());
                return;
            }

            intent.putExtra("CHAT_NAME", session.getName());
            intent.putExtra("CHAT_ID", session.getId());
            intent.putExtra("CHAT_USERNAME", targetUsername);
            startActivity(intent);
        });
    }

    /**
     * [核心改进] 将数据库消息转换为会话列表
     * 优先使用好友备注名称
     */
    private List<Session> convertToSessions(List<MessageEntity> messages) {
        if (messages == null || messages.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Integer, Session> sessionMap = new HashMap<>();

        for (MessageEntity msg : messages) {
            int otherId;
            String otherName = null;
            String otherAvatar = null;

            if (msg.senderId == myUserId) {
                // 我发送的消息，对方是接收者
                otherId = msg.receiverId != null ? msg.receiverId : 0;

                // [核心改进] 从缓存中查找好友信息
                FriendInfo friendInfo = findFriendInfoByUserId(otherId);
                if (friendInfo != null) {
                    otherName = friendInfo.displayName;
                    otherAvatar = friendInfo.avatarUrl;
                    Log.d(TAG, "从缓存获取好友信息: userId=" + otherId + ", name=" + otherName);
                } else {
                    otherName = msg.receiverNickname;
                    otherAvatar = msg.receiverAvatarUrl;
                }
            } else {
                // 我接收的消息，对方是发送者
                otherId = msg.senderId != null ? msg.senderId : 0;

                // [核心改进] 从缓存中查找好友信息
                FriendInfo friendInfo = findFriendInfoByUserId(otherId);
                if (friendInfo != null) {
                    otherName = friendInfo.displayName;
                    otherAvatar = friendInfo.avatarUrl;
                    Log.d(TAG, "从缓存获取好友信息: userId=" + otherId + ", name=" + otherName);
                } else {
                    otherName = msg.senderNickname;
                    otherAvatar = msg.senderAvatarUrl;
                }
            }

            if (otherId == 0) continue;

            if (!sessionMap.containsKey(otherId)) {
                String displayName = (otherName != null && !otherName.isEmpty()) ? otherName : "用户 " + otherId;

                Session session = new Session(String.valueOf(otherId), displayName, msg.content != null ? msg.content : "", formatTime(msg.createAt), otherAvatar);

                sessionMap.put(otherId, session);

                Log.d(TAG, "创建会话: " + displayName + ", userId=" + otherId);
            }
        }

        return new ArrayList<>(sessionMap.values());
    }

    /**
     * [新增] 根据userId查找好友信息
     */
    private FriendInfo findFriendInfoByUserId(int userId) {
        for (FriendInfo info : friendInfoMap.values()) {
            if (info.userId == userId) {
                return info;
            }
        }
        return null;
    }

    private String formatTime(String createAt) {
        if (createAt == null) return "";

        if (createAt.length() >= 16) {
            return createAt.substring(11, 16);
        }

        return createAt;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * [新增] 好友信息内部类
     */
    private static class FriendInfo {
        String displayName;
        String avatarUrl;
        int userId;
    }
}