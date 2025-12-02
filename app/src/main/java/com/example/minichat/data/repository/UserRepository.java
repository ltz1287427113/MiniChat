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

import java.io.File;
import android.webkit.MimeTypeMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
     * 统一更新入口
     * @param nickname 若不为 null，则调用修改昵称接口
     * @param avatarPath 若不为 null，则调用修改头像接口
     */
    public void updateUser(String nickname, String avatarPath, MutableLiveData<String> resultLiveData) {

        // --- 情况 A: 修改昵称 (JSON) ---
        if (nickname != null) {
            UserUpdateRequest request = new UserUpdateRequest(nickname);
            apiService.updateUserInfo(request).enqueue(new UpdateCallback(resultLiveData));
        }

        // --- 情况 B: 修改头像 (Multipart) ---
        else if (avatarPath != null) {
            File file = new File(avatarPath);
            if (file.exists()) {
                // 1. 创建 RequestBody (文件内容)
                String mimeType = getMimeType(file.getAbsolutePath());
                RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);

                // 2. 创建 MultipartBody.Part (表单项)
                // [注意] 第一个参数 "avatar" 必须和后端接口的 @RequestParam("avatar") 一致！
                MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

                // 3. 调用上传接口
                apiService.updateAvatar(body).enqueue(new UpdateCallback(resultLiveData));
            } else {
                resultLiveData.postValue("文件不存在");
            }
        }
    }

    private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type != null ? type : "application/octet-stream";
    }

    /**
     * 内部复用的 Callback
     * 因为两个接口返回的数据结构 (UserUpdateResponse) 是一样的，所以可以复用回调逻辑
     */
    private class UpdateCallback implements Callback<ResponseMessage<UserUpdateResponse>> {
        private MutableLiveData<String> resultLiveData;

        public UpdateCallback(MutableLiveData<String> resultLiveData) {
            this.resultLiveData = resultLiveData;
        }

        @Override
        public void onResponse(Call<ResponseMessage<UserUpdateResponse>> call, Response<ResponseMessage<UserUpdateResponse>> response) {
            if (response.isSuccessful() && response.body() != null) {
                if (response.body().isSuccess()) {
                    // 1. 成功拿到最新数据
                    UserUpdateResponse newData = response.body().getData();
                    // 2. 更新本地缓存 (昵称或头像)
                    updateLocalUser(newData);
                    // 3. 通知 UI
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
    }

    // 更新本地 SpUtils 数据
    private void updateLocalUser(UserUpdateResponse newData) {
        UserLoginResponse localUser = SpUtils.getUser(context);
        if (localUser != null) {
            // 如果返回了新昵称，就更新本地昵称
            if (newData.getNickname() != null) {
                localUser.setNickname(newData.getNickname());
            }
            // 如果返回了新头像 URL，就更新本地头像
            if (newData.getAvatarUrl() != null) {
                localUser.setAvatarUrl(newData.getAvatarUrl());
            }
            SpUtils.saveUser(context, localUser);
        }
    }
}
