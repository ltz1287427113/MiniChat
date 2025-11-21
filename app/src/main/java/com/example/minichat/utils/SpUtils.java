package com.example.minichat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.minichat.data.model.UserLoginResponse;
import com.google.gson.Gson;

public class SpUtils {
    private static final String SP_NAME = "minichat_config";
    private static final String KEY_TOKEN = "sp_token";
    private static final String KEY_USER_INFO = "sp_user_info";

    private static SharedPreferences getSp(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    // 1. 保存 Token
    public static void saveToken(Context context, String token) {
        getSp(context).edit().putString(KEY_TOKEN, token).apply();
    }

    // 2. 获取 Token
    public static String getToken(Context context) {
        return getSp(context).getString(KEY_TOKEN, null);
    }

    // 3. 保存用户信息 (User 对象)
    // 我们把 User 对象转换成 JSON 字符串保存
    public static void saveUser(Context context, UserLoginResponse user) {
        String userJson = new Gson().toJson(user);
        getSp(context).edit().putString(KEY_USER_INFO, userJson).apply();
    }

    // 4. 获取用户信息
    public static UserLoginResponse getUser(Context context) {
        String userJson = getSp(context).getString(KEY_USER_INFO, null);
        if (userJson != null) {
            return new Gson().fromJson(userJson, UserLoginResponse.class);
        }
        return null;
    }

    // 5. 退出登录 (清空数据)
    public static void logout(Context context) {
        getSp(context).edit().clear().apply();
    }

    // 6. 检查是否已登录
    public static boolean isLoggedIn(Context context) {
        return getToken(context) != null;
    }
}