package com.example.minichat.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.minichat.data.repository.UserRepository;

public class UserViewModel extends AndroidViewModel {

    private UserRepository repository;
    private MutableLiveData<String> updateResult = new MutableLiveData<>();

    public UserViewModel(@NonNull Application application) {
        super(application);
        // 初始化 Repository
        repository = new UserRepository(application);
    }

    public LiveData<String> getUpdateResult() {
        return updateResult;
    }

    /**
     * ViewModel 只是一个“二传手”
     * 它接收 Activity 的指令，转发给 Repository
     */
    public void updateUser(String nickname, String avatarUrl) {
        repository.updateUser(nickname, avatarUrl, updateResult);
    }
}