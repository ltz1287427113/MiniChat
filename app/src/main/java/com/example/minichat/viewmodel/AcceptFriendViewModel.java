package com.example.minichat.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.minichat.data.repository.AuthRepository;
import com.example.minichat.data.repository.FriendRepository;

public class AcceptFriendViewModel extends AndroidViewModel {

    private FriendRepository repository;
    private MutableLiveData<AuthRepository.Result<String>> handleResult = new MutableLiveData<>();

    public AcceptFriendViewModel(@NonNull Application application) {
        super(application);
        repository = new FriendRepository(application);
    }

    public LiveData<AuthRepository.Result<String>> getHandleResult() {
        return handleResult;
    }

    public void acceptFriend(int applicationId, String remark) {
        // 调用仓库，状态固定为 "ACCEPTED"
        repository.handleFriendApplication(applicationId, "ACCEPTED", remark, handleResult);
    }
}