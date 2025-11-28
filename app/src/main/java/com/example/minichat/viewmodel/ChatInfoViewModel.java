package com.example.minichat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.minichat.data.repository.AuthRepository;
import com.example.minichat.data.repository.FriendRepository;

public class ChatInfoViewModel extends AndroidViewModel {

    private FriendRepository friendRepository;
    private MutableLiveData<AuthRepository.Result<String>> deleteFriendResult = new MutableLiveData<>();
    private MutableLiveData<AuthRepository.Result<String>> updateFriendremarkResult = new MutableLiveData<>();

    public ChatInfoViewModel(@NonNull Application application) {
        super(application);
        friendRepository = new FriendRepository(application);
    }

    public LiveData<AuthRepository.Result<String>> getDeleteFriendResult() {
        return deleteFriendResult;
    }

    public void deleteFriend(String friendUsername) {
        friendRepository.deleteFriend(friendUsername, deleteFriendResult);
    }

    public LiveData<AuthRepository.Result<String>> getUpdateFriendremarkResult() {
        return updateFriendremarkResult;
    }

    public void updateFriendremark(String username, String newRemark) {
        friendRepository.updateFriendremark(username, newRemark, updateFriendremarkResult);
    }
}