package com.example.minichat.data.remote;

import com.example.minichat.data.model.response.JwtResponse;
import com.example.minichat.data.model.response.ResponseMessage;
import com.example.minichat.data.model.request.SendCodeRequest;
import com.example.minichat.data.model.request.UserLoginRequest;
import com.example.minichat.data.model.request.UserRegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // 1. 登录 (返回 Token)
    @POST("auth/login")
    Call<ResponseMessage<JwtResponse>> login(@Body UserLoginRequest request);
    // 2.注册 (data 为 null)
    @POST("auth/register")
    Call<ResponseMessage<String>> register(@Body UserRegisterRequest request);
    // 3. [新] 发送验证码 (data 为 null，泛型用 Object 或 Void)
    @POST("auth/send-code")
    Call<ResponseMessage<String>> sendCode(@Body SendCodeRequest request);


}