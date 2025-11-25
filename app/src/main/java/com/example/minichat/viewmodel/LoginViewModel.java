package com.example.minichat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.minichat.data.model.response.JwtResponse;
import com.example.minichat.data.repository.AuthRepository;

public class LoginViewModel extends AndroidViewModel {

    private AuthRepository repository;

    // 这里的泛型变成了 LoginData (因为 API 只返回 Token)
    private MutableLiveData<AuthRepository.Result<JwtResponse>> loginResult = new MutableLiveData<>();

    private MutableLiveData<AuthRepository.Result<JwtResponse>> updateTokenResult = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthRepository(application); // 传入 Context
    }

    public LiveData<AuthRepository.Result<JwtResponse>> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        repository.login(username, password, loginResult);
    }

    public LiveData<AuthRepository.Result<JwtResponse>> getUpdateTokenResult() {
        return updateTokenResult;
    }

    public void updateToken(String token) {
        repository.updateToken(token, updateTokenResult);
    }
}