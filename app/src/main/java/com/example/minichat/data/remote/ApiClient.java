package com.example.minichat.data.remote;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // [修改] 你的后端地址
    private static final String BASE_URL = "http://192.168.1.157:8080/";

    private static Retrofit retrofit = null;

    // [修改] 我们需要传入 Context 来初始化拦截器
    public static ApiService getApiService(Context context) {
        if (retrofit == null) {

            // 1. 创建 OkHttpClient 并添加拦截器
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new TokenInterceptor(context)) // [核心] 添加拦截器
                    .connectTimeout(30, TimeUnit.SECONDS) // 连接超时
                    .readTimeout(30, TimeUnit.SECONDS)    // 读取超时
                    .writeTimeout(30, TimeUnit.SECONDS)   // 写入超时
                    .build();

            // 2. 创建 Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // [核心] 绑定 OkHttp
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}