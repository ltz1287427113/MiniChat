package com.example.minichat.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.minichat.data.repository.AuthRepository;
import com.example.minichat.data.repository.FriendRepository;

public class AddFriendRequestViewModel extends AndroidViewModel {

    private FriendRepository friendRepository;
    private MutableLiveData<AuthRepository.Result<String>> addFriendResult = new MutableLiveData<>();

    public AddFriendRequestViewModel(@NonNull Application application) {
        super(application);
        friendRepository = new FriendRepository(application);
    }

    public LiveData<AuthRepository.Result<String>> getAddFriendResult() {
        return addFriendResult;
    }

    public void sendFriendRequest(String targetUsername, String greeting, String remark) {
        friendRepository.sendFriendRequest(targetUsername, greeting, remark, addFriendResult);
    }
}