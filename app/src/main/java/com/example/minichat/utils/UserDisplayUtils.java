package com.example.minichat.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.minichat.R;
import com.example.minichat.data.remote.ApiClient;

public class UserDisplayUtils {

    /**
     * 加载用户头像到指定的 ImageView。
     * 会处理 URL 拼接、占位图、错误图和圆形裁剪。
     *
     * @param context 上下文，用于 Glide
     * @param avatarUrl 用户的头像 URL
     * @param imageView 显示头像的 ImageView
     */
    public static void loadAvatar(Context context, String avatarUrl, ImageView imageView) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            String baseUrl = ApiClient.BASE_URL;
            // 确保 URL 拼接正确，避免双斜杠或缺少斜杠
            if (baseUrl.endsWith("/") && avatarUrl.startsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            } else if (!baseUrl.endsWith("/") && !avatarUrl.startsWith("/")) {
                baseUrl = baseUrl + "/";
            }
            String fullAvatarUrl = baseUrl + avatarUrl;

            Glide.with(context)
                    .load(fullAvatarUrl)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .circleCrop()
                    .into(imageView);
        } else {
            // 如果没有 URL，显示默认头像
            imageView.setImageResource(R.mipmap.ic_launcher_round);
        }
    }
}
