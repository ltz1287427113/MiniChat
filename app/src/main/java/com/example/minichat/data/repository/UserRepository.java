package com.example.minichat.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;

import com.example.minichat.data.model.request.UserUpdateRequest;
import com.example.minichat.data.model.response.ResponseMessage;
import com.example.minichat.data.model.response.UserLoginResponse;
import com.example.minichat.data.model.response.UserUpdateResponse;
import com.example.minichat.data.remote.ApiClient;
import com.example.minichat.data.remote.ApiService;
import com.example.minichat.utils.SpUtils; //

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 用户数据仓库
 * 负责处理与用户相关的数据操作 (网络 + 本地)
 */
public class UserRepository {

    private ApiService apiService;
    private Context context;

    public UserRepository(Context context) {
        this.context = context;
        this.apiService = ApiClient.getApiService(context);
    }

    /**
     * 更新用户信息
     * @param nickname 新昵称
     * @param avatarUrl 新头像
     * @param resultLiveData 用于返回结果给 ViewModel 的通道
     */
    public void updateUser(String nickname, String avatarUrl, MutableLiveData<String> resultLiveData) {
        UserUpdateRequest request = new UserUpdateRequest(nickname, avatarUrl);

        apiService.updateUser(request).enqueue(new Callback<ResponseMessage<UserUpdateResponse>>() {
            @Override
            public void onResponse(Call<ResponseMessage<UserUpdateResponse>> call, Response<ResponseMessage<UserUpdateResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        // 1. 网络请求成功，获取最新数据
                        UserUpdateResponse newData = response.body().getData();

                        // 2. [核心逻辑] Repository 负责更新本地缓存 (SpUtils)
                        updateLocalUser(newData);

                        // 3. 通知 ViewModel 任务完成
                        resultLiveData.postValue("SUCCESS");
                    } else {
                        resultLiveData.postValue(response.body().getMessage());
                    }
                } else {
                    resultLiveData.postValue("请求失败: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage<UserUpdateResponse>> call, Throwable t) {
                resultLiveData.postValue("网络错误: " + t.getMessage());
            }
        });
    }

    /**
     * 辅助方法：更新本地 SharedPreferences
     * (这部分逻辑属于数据持久化，理应在 Repository 层处理)
     */
    private void updateLocalUser(UserUpdateResponse newData) {
        UserLoginResponse localUser = SpUtils.getUser(context);
        if (localUser != null) {
            if (newData.getNickname() != null) {
                localUser.setNickname(newData.getNickname());
            }
            if (newData.getAvatarUrl() != null) {
                localUser.setAvatarUrl(newData.getAvatarUrl());
            }
            SpUtils.saveUser(context, localUser);
        }
    }
}