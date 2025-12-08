package com.example.minichat.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.minichat.data.model.request.AddFriendRequest;
import com.example.minichat.data.model.request.ScanRequest;
import com.example.minichat.data.model.request.UpdateFriendremarkRequest;
import com.example.minichat.data.model.response.ApplicationResponse;
import com.example.minichat.data.model.response.FriendDetailResponse;
import com.example.minichat.data.model.response.FriendListGroupedResponse;
import com.example.minichat.data.model.response.ResponseMessage;
import com.example.minichat.data.model.response.ScanResponse;
import com.example.minichat.data.model.response.StrangerResponse;
import com.example.minichat.data.remote.ApiClient;
import com.example.minichat.data.remote.ApiService;
import com.example.minichat.data.repository.AuthRepository.Result;

import com.example.minichat.data.model.request.HandleFriendApplicationRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendRepository {

    private ApiService apiService;

    public FriendRepository(Context context) {
        apiService = ApiClient.getApiService(context);
    }

    /**
     * 处理好友请求
     *
     * @param applicationId
     * @param status
     * @param remark
     * @param resultLiveData
     */
    public void handleFriendApplication(int applicationId, String status, String remark, MutableLiveData<Result<String>> resultLiveData) {
        HandleFriendApplicationRequest request = new HandleFriendApplicationRequest(applicationId, status, remark);
        apiService.handleFriendApplication(request).enqueue(new Callback<ResponseMessage<Void>>() {
            @Override
            public void onResponse(Call<ResponseMessage<Void>> call, Response<ResponseMessage<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        resultLiveData.postValue(Result.success(null));
                    } else {
                        resultLiveData.postValue(Result.failure(new Exception(response.body().getMessage())));
                    }
                } else {
                    resultLiveData.postValue(Result.failure(new Exception("请求失败: " + response.code())));
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage<Void>> call, Throwable t) {
                resultLiveData.postValue(Result.failure(new Exception("网络错误: " + t.getMessage())));
            }
        });
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

    /**
     * 判断是否为好友
     */
    public void isFriend(String usernameOrEmail, MutableLiveData<Result<Boolean>> resultLiveData) {
        apiService.isFriend(usernameOrEmail).enqueue(new Callback<ResponseMessage<Boolean>>() {
            @Override
            public void onResponse(Call<ResponseMessage<Boolean>> call, Response<ResponseMessage<Boolean>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        resultLiveData.postValue(Result.success(response.body().getData()));
                    } else {
                        resultLiveData.postValue(Result.failure(new Exception(response.body().getMessage())));
                    }
                } else {
                    resultLiveData.postValue(Result.failure(new Exception("请求失败: " + response.code())));
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage<Boolean>> call, Throwable t) {
                resultLiveData.postValue(Result.failure(new Exception("网络错误: " + t.getMessage())));
            }
        });
    }

    /**
     * 发送好友申请
     *
     * @param targetUsername 目标微信号 (搜索结果里的 username)
     * @param greeting       打招呼内容
     * @param remark         备注
     */
    public void sendFriendRequest(String targetUsername, String greeting, String remark, MutableLiveData<Result<String>> resultLiveData) {
        // 构建请求体 (这里我们只用 username，email 传 null)
        AddFriendRequest request = new AddFriendRequest(targetUsername, null, greeting, remark);

        apiService.addFriend(request).enqueue(new Callback<ResponseMessage<Object>>() {
            @Override
            public void onResponse(Call<ResponseMessage<Object>> call, Response<ResponseMessage<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        resultLiveData.postValue(Result.success("好友请求已发送"));
                    } else {
                        resultLiveData.postValue(Result.failure(new Exception(response.body().getMessage())));
                    }
                } else {
                    resultLiveData.postValue(Result.failure(new Exception("请求失败: " + response.code())));
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage<Object>> call, Throwable t) {
                resultLiveData.postValue(Result.failure(new Exception("网络错误: " + t.getMessage())));
            }
        });
    }

    /**
     * 获取好友申请列表
     */
    public void getFriendApplications(MutableLiveData<Result<List<ApplicationResponse>>> resultLiveData) {
        apiService.getFriendApplications().enqueue(new Callback<ResponseMessage<List<ApplicationResponse>>>() {
            @Override
            public void onResponse(Call<ResponseMessage<List<ApplicationResponse>>> call, Response<ResponseMessage<List<ApplicationResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        resultLiveData.postValue(Result.success(response.body().getData()));
                    } else {
                        resultLiveData.postValue(Result.failure(new Exception(response.body().getMessage())));
                    }
                } else {
                    resultLiveData.postValue(Result.failure(new Exception("请求失败: " + response.code())));
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage<List<ApplicationResponse>>> call, Throwable t) {
                resultLiveData.postValue(Result.failure(new Exception("网络错误: " + t.getMessage())));
            }
        });
    }

    /**
     * 获取好友列表
     */
    public void getFriendList(MutableLiveData<Result<FriendListGroupedResponse>> resultLiveData) {
        apiService.getFriends().enqueue(new Callback<ResponseMessage<FriendListGroupedResponse>>() {
            @Override
            public void onResponse(Call<ResponseMessage<FriendListGroupedResponse>> call, Response<ResponseMessage<FriendListGroupedResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Log.d("FriendRepository", "getFriendList success: " + response.body().getData());
                        resultLiveData.postValue(Result.success(response.body().getData()));
                    } else {
                        Log.e("FriendRepository", "getFriendList business error: " + response.body().getMessage());
                        resultLiveData.postValue(Result.failure(new Exception(response.body().getMessage())));
                    }
                } else {
                    Log.e("FriendRepository", "getFriendList request failed: " + response.code());
                    resultLiveData.postValue(Result.failure(new Exception("请求失败: " + response.code())));
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage<FriendListGroupedResponse>> call, Throwable t) {
                Log.e("FriendRepository", "getFriendList network error: " + t.getMessage(), t);
                resultLiveData.postValue(Result.failure(new Exception("网络错误: " + t.getMessage())));
            }
        });
    }

    /**
     * 获取好友详情
     */
    public void getFriendDetail(String username, MutableLiveData<Result<FriendDetailResponse>> resultLiveData) {
        apiService.getFriendDetail(username).enqueue(new Callback<ResponseMessage<FriendDetailResponse>>() {
            @Override
            public void onResponse(Call<ResponseMessage<FriendDetailResponse>> call, Response<ResponseMessage<FriendDetailResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        resultLiveData.postValue(Result.success(response.body().getData()));
                    } else {
                        resultLiveData.postValue(Result.failure(new Exception(response.body().getMessage())));
                    }
                } else {
                    resultLiveData.postValue(Result.failure(new Exception("请求失败: " + response.code())));
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage<FriendDetailResponse>> call, Throwable t) {
                resultLiveData.postValue(Result.failure(new Exception("网络错误: " + t.getMessage())));
            }
        });
    }


    /**
     * 更改好友备注
     */
    public void updateFriendremark(String username, String newRemark, MutableLiveData<Result<String>> resultLiveData) {
        UpdateFriendremarkRequest request = new UpdateFriendremarkRequest(username, newRemark);
        apiService.updateFriendremark(request).enqueue(new Callback<ResponseMessage<String>>() {

            @Override
            public void onResponse(Call<ResponseMessage<String>> call, Response<ResponseMessage<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        resultLiveData.postValue(Result.success(response.body().getData()));
                    } else {
                        resultLiveData.postValue(Result.failure(new Exception(response.body().getMessage())));
                    }
                } else {
                    resultLiveData.postValue(Result.failure(new Exception("请求失败: " + response.code())));
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage<String>> call, Throwable t) {
                resultLiveData.postValue(Result.failure(new Exception("网络错误: " + t.getMessage())));
            }
        });
    }

    /**
     * 删除好友
     */
    public void deleteFriend(String friendUsername, MutableLiveData<Result<String>> resultLiveData) {
        apiService.deleteFriend(friendUsername).enqueue(new Callback<ResponseMessage<String>>() {

            @Override
            public void onResponse(Call<ResponseMessage<String>> call, Response<ResponseMessage<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        resultLiveData.postValue(Result.success(response.body().getData()));
                    } else {
                        resultLiveData.postValue(Result.failure(new Exception(response.body().getMessage())));
                    }
                } else {
                    resultLiveData.postValue(Result.failure(new Exception("请求失败: " + response.code())));
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage<String>> call, Throwable t) {
                resultLiveData.postValue(Result.failure(new Exception("网络错误: " + t.getMessage())));
            }
        });
    }

    /**
     * 扫描二维码
     */
    public void scanQrcode(String content, int currentUserId, String message, String remark, MutableLiveData<Result<ScanResponse>> resultLiveData) {
        ScanRequest request = new ScanRequest(content, currentUserId, message, remark);

        apiService.scanQrcode(request).enqueue(new Callback<ResponseMessage<ScanResponse>>() {
            @Override
            public void onResponse(Call<ResponseMessage<ScanResponse>> call, Response<ResponseMessage<ScanResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        resultLiveData.postValue(Result.success(response.body().getData()));
                    } else {
                        resultLiveData.postValue(Result.failure(new Exception(response.body().getMessage())));
                    }
                } else {
                    resultLiveData.postValue(Result.failure(new Exception("请求失败: " + response.code())));
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage<ScanResponse>> call, Throwable t) {
                resultLiveData.postValue(Result.failure(new Exception("网络错误: " + t.getMessage())));
            }
        });
    }
}