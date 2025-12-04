package com.example.minichat.utils;

import android.util.Log;
import com.example.minichat.data.model.response.ChatMessage;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class WebSocketManager {

    private static WebSocketManager instance;
    private StompClient mStompClient;
    private CompositeDisposable compositeDisposable;
    private final Gson gson = new Gson();

    // 🔥 关键修改：使用 ws:// 协议（不是 http://）
    private static final String WS_URL = "ws://192.168.1.154:8080/ws/stomp";

    public interface OnMessageReceivedListener {
        void onMessageReceived(ChatMessage message);
    }

    private final List<OnMessageReceivedListener> listeners = new ArrayList<>();

    private WebSocketManager() {}

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    public void connect(String token) {
        if (mStompClient != null && mStompClient.isConnected()) {
            Log.d("WebSocket", "⚠️ 已经连接，跳过");
            return;
        }

        Log.d("WebSocket", "🚀 开始连接 WebSocket（原生模式）...");
        compositeDisposable = new CompositeDisposable();

        // 🔥 把 token 放到 URL 参数
        String wsUrlWithToken = WS_URL + "?token=" + token;
        Log.d("WebSocket", "📍 完整URL: " + wsUrlWithToken);

        // 🔥 使用 OKHTTP 连接（原生 WebSocket）
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrlWithToken);

        // 设置心跳（可选）
        mStompClient.withClientHeartbeat(10000).withServerHeartbeat(10000);

        // STOMP 连接头（可选，但建议保留）
        List<StompHeader> stompHeaders = new ArrayList<>();
        stompHeaders.add(new StompHeader("Authorization", "Bearer " + token));

        Log.d("WebSocket", "📡 开始执行 connect()...");
        mStompClient.connect(stompHeaders);

        // 监听连接状态
        Disposable dispLifecycle = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    Log.d("WebSocket", "📬 收到生命周期事件: " + lifecycleEvent.getType());
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d("WebSocket", "✅ 连接成功！");
                            subscribeToPrivate();
                            break;
                        case ERROR:
                            Log.e("WebSocket", "❌ 连接出错", lifecycleEvent.getException());
                            if (lifecycleEvent.getException() != null) {
                                lifecycleEvent.getException().printStackTrace();
                            }
                            break;
                        case CLOSED:
                            Log.d("WebSocket", "🔌 连接断开");
                            break;
                    }
                }, throwable -> {
                    Log.e("WebSocket", "❌ lifecycle 订阅失败", throwable);
                    throwable.printStackTrace();
                });

        compositeDisposable.add(dispLifecycle);
        Log.d("WebSocket", "✅ lifecycle 订阅完成");
    }

    private void subscribeToPrivate() {
        Log.d("WebSocket", "📡 订阅私聊频道: /user/queue/chat");

        Disposable dispTopic = mStompClient.topic("/user/queue/chat")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    String json = topicMessage.getPayload();
                    Log.d("WebSocket", "📨 收到新消息: " + json);

                    try {
                        ChatMessage message = gson.fromJson(json, ChatMessage.class);
                        notifyListeners(message);
                    } catch (Exception e) {
                        Log.e("WebSocket", "❌ 解析消息失败", e);
                    }
                }, throwable -> {
                    Log.e("WebSocket", "❌ 订阅失败", throwable);
                    throwable.printStackTrace();
                });

        compositeDisposable.add(dispTopic);
    }

    public void sendMessage(ChatMessage message) {
        String json = gson.toJson(message);
        Log.d("WebSocket", "📤 发送消息: " + json);

        Disposable dispSend = mStompClient.send("/send", json)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> Log.d("WebSocket", "✅ 发送成功"),
                        e -> Log.e("WebSocket", "❌ 发送失败", e)
                );

        compositeDisposable.add(dispSend);
    }

    public void disconnect() {
        if (mStompClient != null) {
            mStompClient.disconnect();
        }
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    public void addListener(OnMessageReceivedListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(OnMessageReceivedListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(ChatMessage message) {
        for (OnMessageReceivedListener listener : listeners) {
            listener.onMessageReceived(message);
        }
    }
}