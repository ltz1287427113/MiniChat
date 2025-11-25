package com.example.minichat.data.remote; // (确保这是你的包名)

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.minichat.activity.LoginActivity;
import com.example.minichat.utils.SpUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Token 拦截器
 * 1. 自动在请求头里加上 Token。
 * 2. 监听 401 错误，如果 Token 过期自动跳转登录页。
 */
public class TokenInterceptor implements Interceptor {

    private Context context;

    public TokenInterceptor(Context context) {
        // 这里的 Context 必须是 Application Context，防止内存泄漏
        this.context = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // 1. 获取本地保存的 Token
        String token = SpUtils.getToken(context);


        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder();

        // 2. 如果有 Token，就自动加到请求头里
        // (这样你就不用在每个 API 方法里手动传 @Header 了)
        if (token != null && !token.isEmpty()) {
            // 注意：通常后端要求的前缀是 "Bearer "
            builder.header("Authorization", "Bearer " + token);
        }

        Request newRequest = builder.build();


        // 3. 执行请求
        Response response = chain.proceed(newRequest);

        // 4. [核心] 检查 Token 是否过期 (HTTP 401)
        if (response.code() == 401) {
            Log.e("TokenInterceptor", "Token 已过期或无效，强制退出");

            // a. 清空本地存储的用户信息
            SpUtils.logout(context);

            // b. 强制跳转回登录页
            Intent intent = new Intent(context, LoginActivity.class);
            // 添加标志位：清空任务栈 (让用户按返回键回不去主页)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }

        return response;
    }
}