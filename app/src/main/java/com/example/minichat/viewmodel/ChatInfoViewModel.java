package com.example.minichat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.minichat.data.model.response.FriendDetailResponse;
import com.example.minichat.data.repository.AuthRepository;
import com.example.minichat.data.repository.FriendRepository;

/**
 * [增强版] ChatInfoViewModel
 * 功能：
 * 1. 加载好友详情
 * 2. 更新好友备注
 * 3. 删除好友
 * 4. [TODO] 清空聊天记录
 */
public class ChatInfoViewModel extends AndroidViewModel {

    private FriendRepository friendRepository;

    // LiveData - 好友详情
    private MutableLiveData<AuthRepository.Result<FriendDetailResponse>> friendDetailResult = new MutableLiveData<>();

    // LiveData - 删除好友结果
    private MutableLiveData<AuthRepository.Result<String>> deleteFriendResult = new MutableLiveData<>();

    // LiveData - 更新备注结果
    private MutableLiveData<AuthRepository.Result<String>> updateFriendremarkResult = new MutableLiveData<>();

    public ChatInfoViewModel(@NonNull Application application) {
        super(application);
        friendRepository = new FriendRepository(application);
    }

    /**
     * [新增] 加载好友详情
     */
    public void loadFriendDetail(String username) {
        friendRepository.getFriendDetail(username, friendDetailResult);
    }

    /**
     * 获取好友详情结果
     */
    public LiveData<AuthRepository.Result<FriendDetailResponse>> getFriendDetailResult() {
        return friendDetailResult;
    }

    /**
     * 删除好友
     */
    public void deleteFriend(String friendUsername) {
        friendRepository.deleteFriend(friendUsername, deleteFriendResult);
    }

    /**
     * 获取删除好友结果
     */
    public LiveData<AuthRepository.Result<String>> getDeleteFriendResult() {
        return deleteFriendResult;
    }

    /**
     * 更新好友备注
     */
    public void updateFriendremark(String username, String newRemark) {
        friendRepository.updateFriendremark(username, newRemark, updateFriendremarkResult);
    }

    /**
     * 获取更新备注结果
     */
    public LiveData<AuthRepository.Result<String>> getUpdateFriendremarkResult() {
        return updateFriendremarkResult;
    }

    /**
     * [TODO] 清空聊天记录
     * 需要在ChatRepository中添加对应方法
     */
    public void clearChatHistory(int friendId) {
        // TODO: 实现清空聊天记录功能
        // chatRepository.clearHistory(myUserId, friendId);
    }
}