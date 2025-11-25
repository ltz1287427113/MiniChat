package com.example.minichat.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.minichat.data.model.response.StrangerResponse;
import com.example.minichat.data.repository.AuthRepository;
import com.example.minichat.data.repository.FriendRepository;

public class SearchUserViewModel extends AndroidViewModel {

    private FriendRepository repository;
    private MutableLiveData<AuthRepository.Result<StrangerResponse>> searchResult = new MutableLiveData<>();

    public SearchUserViewModel(@NonNull Application application) {
        super(application);
        repository = new FriendRepository(application);
    }

    public LiveData<AuthRepository.Result<StrangerResponse>> getSearchResult() {
        return searchResult;
    }

    public void search(String keyword) {
        repository.searchStranger(keyword, searchResult);
    }
}