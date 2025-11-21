package com.example.minichat.data.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // [重要] 替换成你电脑的 IP 地址 (如果你在本地跑后端)
    // Android 模拟器访问电脑本机用 10.0.2.2
    // 真机调试请用电脑的局域网 IP (例如 192.168.1.5)
    private static final String BASE_URL = "http://192.168.1.170:8080/";

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // 使用 Gson 解析 JSON
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}