package com.example.minichat.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.minichat.data.model.LoginData;
import com.example.minichat.data.model.ResponseMessage;
import com.example.minichat.data.model.SendCodeRequest;
import com.example.minichat.data.model.UserLoginRequest;
import com.example.minichat.data.model.UserRegisterRequest;
import com.example.minichat.data.remote.ApiClient;
import com.example.minichat.data.remote.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private ApiService apiService;

    public AuthRepository() {
        apiService = ApiClient.getApiService();
    }

    /**
     * 登录方法
     * @param username 邮箱
     * @param password 密码
     * @param resultData 用于返回结果 (String 为 Token)
     */
    // [修改] 泛型改为 LoginData
    public void login(String username, String password, MutableLiveData<Result<LoginData>> resultData) {
        UserLoginRequest request = new UserLoginRequest(username, password);

        apiService.login(request).enqueue(new Callback<ResponseMessage<LoginData>>() {
            @Override
            public void onResponse(Call<ResponseMessage<LoginData>> call, Response<ResponseMessage<LoginData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseMessage<LoginData> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        // [修改] 成功！直接返回整个 LoginData 对象
                        resultData.postValue(Result.success(apiResponse.getData()));
                    } else {
                        resultData.postValue(Result.failure(new Exception(apiResponse.getMessage())));
                    }
                } else {
                    resultData.postValue(Result.failure(new Exception("服务器错误: " + response.code())));
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage<LoginData>> call, Throwable t) {
                resultData.postValue(Result.failure(new Exception("网络连接失败: " + t.getMessage())));
            }
        });
    }

    /**
     * [新] 发送验证码
     */
    public void sendCode(String email, MutableLiveData<Result<String>> resultData) {
        SendCodeRequest request = new SendCodeRequest(email);

        apiService.sendCode(request).enqueue(new Callback<ResponseMessage<String>>() {
            @Override
            public void onResponse(Call<ResponseMessage<String>> call, Response<ResponseMessage<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        resultData.postValue(Result.success("验证码已发送"));
                    } else {
                        resultData.postValue(Result.failure(new Exception(response.body().getMessage())));
                    }
                } else {
                    resultData.postValue(Result.failure(new Exception("请求失败: " + response.code())));
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage<String>> call, Throwable t) {
                resultData.postValue(Result.failure(new Exception("网络错误: " + t.getMessage())));
            }
        });
    }

    /**
     * [新] 用户注册
     */
    public void register(String username, String email, String password, String code, MutableLiveData<Result<String>> resultData) {
        UserRegisterRequest request = new UserRegisterRequest(username, email, password, code);

        apiService.register(request).enqueue(new Callback<ResponseMessage<String>>() {
            @Override
            public void onResponse(Call<ResponseMessage<String>> call, Response<ResponseMessage<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        resultData.postValue(Result.success("注册成功"));
                    } else {
                        resultData.postValue(Result.failure(new Exception(response.body().getMessage())));
                    }
                } else {
                    resultData.postValue(Result.failure(new Exception("请求失败: " + response.code())));
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage<String>> call, Throwable t) {
                resultData.postValue(Result.failure(new Exception("网络错误: " + t.getMessage())));
            }
        });
    }

    // 结果包装类 (保持不变)
    public static class Result<T> {
        public T data;
        public Exception error;

        public static <T> Result<T> success(T data) {
            Result<T> result = new Result<>();
            result.data = data;
            return result;
        }

        public static <T> Result<T> failure(Exception error) {
            Result<T> result = new Result<>();
            result.error = error;
            return result;
        }
    }
}