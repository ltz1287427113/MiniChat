package com.example.minichat.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.minichat.data.repository.AuthRepository;

public class RegisterViewModel extends ViewModel {

    private AuthRepository repository;

    // 观察发送验证码的结果
    private MutableLiveData<AuthRepository.Result<String>> sendCodeResult = new MutableLiveData<>();
    // 观察注册的结果
    private MutableLiveData<AuthRepository.Result<String>> registerResult = new MutableLiveData<>();

    public RegisterViewModel() {
        repository = new AuthRepository();
    }

    public LiveData<AuthRepository.Result<String>> getSendCodeResult() { return sendCodeResult; }
    public LiveData<AuthRepository.Result<String>> getRegisterResult() { return registerResult; }

    // 发送验证码
    public void sendCode(String email) {
        repository.sendCode(email, sendCodeResult);
    }

    // 注册
    public void register(String username, String email, String password, String code) {
        repository.register(username, email, password, code, registerResult);
    }
}