package com.example.minichat.utils;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class UriUtils {

    /**
     * 将 Uri 复制到应用缓存目录，并返回 File 对象
     */
    public static File uriToFile(Context context, Uri uri) {
        try {
            ContentResolver resolver = context.getContentResolver();

            // 1. 获取文件后缀 (如 .jpg)
            String type = resolver.getType(uri);
            String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(type);
            if (extension == null) extension = "jpg"; // 默认 jpg

            // 2. 在缓存目录创建一个临时文件
            File storageDir = context.getCacheDir();
            File tempFile = File.createTempFile("upload_", "." + extension, storageDir);

            // 3. 将 Uri 的数据流写入这个临时文件
            InputStream inputStream = resolver.openInputStream(uri);
            OutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }

            outputStream.close();
            inputStream.close();

            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}