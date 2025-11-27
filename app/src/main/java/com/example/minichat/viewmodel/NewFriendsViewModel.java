package com.example.minichat.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.minichat.data.model.response.ApplicationResponse;
import com.example.minichat.data.model.response.ResponseMessage;
import com.example.minichat.data.repository.AuthRepository;
import com.example.minichat.data.repository.FriendRepository;

import java.util.List;

public class NewFriendsViewModel extends AndroidViewModel {

    private FriendRepository repository;
    private MutableLiveData<AuthRepository.Result<List<ApplicationResponse>>> applicationsResult = new MutableLiveData<>();
    private MutableLiveData<AuthRepository.Result<Void>> handleApplicationResult = new MutableLiveData<>();

    public NewFriendsViewModel(@NonNull Application application) {
        super(application);
        repository = new FriendRepository(application);
    }

    public LiveData<AuthRepository.Result<List<ApplicationResponse>>> getApplicationsResult() {
        return applicationsResult;
    }

    public LiveData<AuthRepository.Result<Void>> getHandleApplicationResult() {
        return handleApplicationResult;
    }

    public void loadApplications() {
        repository.getFriendApplications(applicationsResult);
    }
}