package com.example.minichat.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.minichat.data.model.response.FriendDetailResponse;
import com.example.minichat.data.repository.AuthRepository;
import com.example.minichat.data.repository.FriendRepository;

public class FriendProfileViewModel extends AndroidViewModel {

    private FriendRepository repository;
    private MutableLiveData<AuthRepository.Result<FriendDetailResponse>> friendDetailResult = new MutableLiveData<>();

    public FriendProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new FriendRepository(application);
    }

    public LiveData<AuthRepository.Result<FriendDetailResponse>> getFriendDetailResult() {
        return friendDetailResult;
    }

    public void loadFriendDetail(String username) {
        repository.getFriendDetail(username, friendDetailResult);
    }
}