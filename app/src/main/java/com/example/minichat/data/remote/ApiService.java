package com.example.minichat.data.remote;

import com.example.minichat.data.model.request.AddFriendRequest;
import com.example.minichat.data.model.request.HandleFriendApplicationRequest;
import com.example.minichat.data.model.request.UserUpdateRequest;
import com.example.minichat.data.model.response.ApplicationResponse;
import com.example.minichat.data.model.response.JwtResponse;
import com.example.minichat.data.model.response.ResponseMessage;
import com.example.minichat.data.model.request.SendCodeRequest;
import com.example.minichat.data.model.request.UserLoginRequest;
import com.example.minichat.data.model.request.UserRegisterRequest;
import com.example.minichat.data.model.response.StrangerResponse;
import com.example.minichat.data.model.response.UserUpdateResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // 登录
    @POST("auth/login")
    Call<ResponseMessage<JwtResponse>> login(@Body UserLoginRequest request);
    // 注册
    @POST("auth/register")
    Call<ResponseMessage<String>> register(@Body UserRegisterRequest request);
    // 发送验证码
    @POST("auth/send-code")
    Call<ResponseMessage<String>> sendCode(@Body SendCodeRequest request);
    // 更新用户信息
    @POST("user/update")
    Call<ResponseMessage<UserUpdateResponse>> updateUser(@Body UserUpdateRequest request);
    // 搜索陌生人
    @GET("friendApplication/searchStranger/{usernameOrEmail}")
    Call<ResponseMessage<StrangerResponse>> searchStranger(@Path("usernameOrEmail") String keyword);
    // 发送好友请求
    @POST("friendApplication/addFriend")
    Call<ResponseMessage<Object>> addFriend(@Body AddFriendRequest request);
    // 获取好友申请列表
    @GET("friendApplication/Applications")
    Call<ResponseMessage<List<ApplicationResponse>>> getFriendApplications();

    // 处理好友申请
    @PUT("friendApplication/handle")
    Call<ResponseMessage<Void>> handleFriendApplication(@Body HandleFriendApplicationRequest request);
}