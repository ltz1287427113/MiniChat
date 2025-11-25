package com.example.minichat.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.minichat.data.model.response.ResponseMessage;
import com.example.minichat.data.model.response.StrangerResponse;
import com.example.minichat.data.remote.ApiClient;
import com.example.minichat.data.remote.ApiService;
import com.example.minichat.data.repository.AuthRepository.Result;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendRepository {

    private ApiService apiService;

    public FriendRepository(Context context) {
        apiService = ApiClient.getApiService(context);
    }

    /**
     * 搜索陌生人
     */
    public void searchStranger(String keyword, MutableLiveData<Result<StrangerResponse>> resultLiveData) {
        apiService.searchStranger(keyword).enqueue(new Callback<ResponseMessage<StrangerResponse>>() {
            @Override
            public void onResponse(Call<ResponseMessage<StrangerResponse>> call, Response<ResponseMessage<StrangerResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseMessage<StrangerResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        // 搜索成功，返回陌生人信息
                        resultLiveData.postValue(Result.success(apiResponse.getData()));
                    } else {
                        // 业务失败 (比如查无此人)
                        resultLiveData.postValue(Result.failure(new Exception(apiResponse.getMessage())));
                    }
                } else {
                    resultLiveData.postValue(Result.failure(new Exception("请求失败: " + response.code())));
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage<StrangerResponse>> call, Throwable t) {
                resultLiveData.postValue(Result.failure(new Exception("网络错误: " + t.getMessage())));
            }
        });
    }
}