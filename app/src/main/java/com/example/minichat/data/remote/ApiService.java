package com.example.minichat.data.remote;

import com.example.minichat.data.model.request.AddFriendRequest;
import com.example.minichat.data.model.request.HandleFriendApplicationRequest;
import com.example.minichat.data.model.request.UpdateFriendremarkRequest;
import com.example.minichat.data.model.request.UserUpdateRequest;
import com.example.minichat.data.model.response.ApplicationResponse;
import com.example.minichat.data.model.response.FriendDetailResponse;
import com.example.minichat.data.model.response.FriendListGroupedResponse;
import com.example.minichat.data.model.response.JwtResponse;
import com.example.minichat.data.model.response.ResponseMessage;
import com.example.minichat.data.model.request.SendCodeRequest;
import com.example.minichat.data.model.request.UserLoginRequest;
import com.example.minichat.data.model.request.UserRegisterRequest;
import com.example.minichat.data.model.response.StrangerResponse;
import com.example.minichat.data.model.response.UserUpdateResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // 1. 登录 (返回 Token)
    @POST("auth/login")
    Call<ResponseMessage<JwtResponse>> login(@Body UserLoginRequest request);

    // 注册
    @POST("auth/register")
    Call<ResponseMessage<String>> register(@Body UserRegisterRequest request);

    // 发送验证码
    @POST("auth/send-code")
    Call<ResponseMessage<String>> sendCode(@Body SendCodeRequest request);

    // 更新用户昵称
    @POST("user/update")
    Call<ResponseMessage<UserUpdateResponse>> updateUserInfo(@Body UserUpdateRequest request);

    // 更新用户头像
    @Multipart
    @POST("user/updateAvatar")
    Call<ResponseMessage<UserUpdateResponse>> updateAvatar(@Part MultipartBody.Part avatar);

    // 搜索陌生人
    @GET("friendApplication/searchStranger/{usernameOrEmail}")
    Call<ResponseMessage<StrangerResponse>> searchStranger(@Path("usernameOrEmail") String keyword);

    //是否为好友
    @GET("friendApplication/isFriend/{usernameOrEmail}")
    Call<ResponseMessage<Boolean>> isFriend(@Path("usernameOrEmail") String usernameOrEmail);

    // 验证Token
    @POST("auth/update-token")
    // 替换成你后端实际的更新token接口路径
    Call<ResponseMessage<JwtResponse>> updateToken(@Header("Authorization") String authorization // 把token作为Authorization请求头
    );

    // 发送好友请求
    @POST("friendApplication/addFriend")
    Call<ResponseMessage<Object>> addFriend(@Body AddFriendRequest request);

    // 获取好友申请列表
    @GET("friendApplication/Applications")
    Call<ResponseMessage<List<ApplicationResponse>>> getFriendApplications();

    // 处理好友申请
    @PUT("friendApplication/handle")
    Call<ResponseMessage<Void>> handleFriendApplication(@Body HandleFriendApplicationRequest request);

    // 获取分组好友列表
    @GET("friendShip/getFriends")
    Call<ResponseMessage<FriendListGroupedResponse>> getFriends();

    // 查询好友详情
    @GET("friendShip/getFriendDetail/{username}")
    Call<ResponseMessage<FriendDetailResponse>> getFriendDetail(@Path("username") String username);

    // 更新好友备注
    @PUT("friendShip/updateFriendremark")
    Call<ResponseMessage<String>> updateFriendremark(@Body UpdateFriendremarkRequest request);

    // 删除好友
    @DELETE("friendShip/deleteFriend/{friendUsername}")
    Call<ResponseMessage<String>> deleteFriend(@Path("friendUsername") String friendUsername);

}