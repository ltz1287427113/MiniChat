package com.example.minichat.data.remote;

import com.example.minichat.data.model.request.UserUpdateRequest;
import com.example.minichat.data.model.response.JwtResponse;
import com.example.minichat.data.model.response.ResponseMessage;
import com.example.minichat.data.model.request.SendCodeRequest;
import com.example.minichat.data.model.request.UserLoginRequest;
import com.example.minichat.data.model.request.UserRegisterRequest;
import com.example.minichat.data.model.response.StrangerResponse;
import com.example.minichat.data.model.response.UserUpdateResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

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
    // 更新用户信息
    @POST("user/update")
    Call<ResponseMessage<UserUpdateResponse>> updateUser(@Body UserUpdateRequest request);
    // 搜索陌生人
    @GET("friendApplication/searchStranger/{usernameOrEmail}")
    Call<ResponseMessage<StrangerResponse>> searchStranger(@Path("usernameOrEmail") String keyword);

    /**
     * 更新Token的接口（按后端实际路径调整）
     * @param authorization 请求头（Bearer + token）
     */
    @POST("api/auth/updateToken") // 替换成你后端实际的更新token接口路径
    Call<ResponseMessage<JwtResponse>> updateToken(
            @Header("Authorization") String authorization // 把token作为Authorization请求头
    );
}