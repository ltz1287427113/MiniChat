package com.example.minichat.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.minichat.data.model.LoginData;
import com.example.minichat.data.repository.AuthRepository;

public class LoginViewModel extends ViewModel {

    private AuthRepository repository;

    // 这里的泛型变成了 LoginData (因为 API 只返回 Token)
    private MutableLiveData<AuthRepository.Result<LoginData>> loginResult = new MutableLiveData<>();

    public LoginViewModel() {
        repository = new AuthRepository();
    }

    public LiveData<AuthRepository.Result<LoginData>> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        repository.login(username, password, loginResult);
    }
}