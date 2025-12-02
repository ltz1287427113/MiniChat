package com.example.minichat.utils;

import android.content.Context;
import android.util.Log;
import android.view.Gravity; // [新导入]
import android.view.View;
import android.view.ContextThemeWrapper; // [新导入]
import androidx.appcompat.widget.PopupMenu; // [新导入：使用 androidx]

import com.example.minichat.R;

import java.lang.reflect.Method;

/**
 * [注释]
 * 这是一个可复用的菜单工具类。
 * 它的作用是显示一个带图标的、位置正确的 PopupMenu。
 */
public class MenuHelper {

    /**
     * [注释]
     * 这是一个静态方法，任何地方都可以调用。
     *
     * @param context    Context (来自 Fragment 的 requireActivity())
     * @param anchorView 菜单“锚定”的视图 (那个“更多”按钮)
     * @param menuResId  要加载的菜单 XML (例如 R.menu.top_nav_menu)
     * @param listener   [核心] 一个回调监听器，Fragment 用它来处理菜单项的点击
     */
    public static void showPopupMenuWithIcons(Context context, View anchorView, int menuResId, PopupMenu.OnMenuItemClickListener listener) {

        // 1. [注释] 使用 androidx.appcompat.widget.PopupMenu
        // 并指定 Gravity.END，让它在按钮下方靠右对齐 (修复位置问题)
        PopupMenu popup = new PopupMenu(new ContextThemeWrapper(context, R.style.CustomPopupMenu), anchorView, Gravity.END);

        // 2. [注释] 加载菜单资源 (例如 top_nav_menu.xml)
        popup.getMenuInflater().inflate(menuResId, popup.getMenu());

        // 3. [注释] 设置点击监听器 (这个 listener 由 Fragment 传入)
        popup.setOnMenuItemClickListener(listener);

        // 4. [注释] 强制显示图标
        // (这是从你的 ChatFragment.java 复制过来的逻辑)
        try {
            Method setOptionalIconsVisible = popup.getMenu().getClass().getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            setOptionalIconsVisible.setAccessible(true);
            setOptionalIconsVisible.invoke(popup.getMenu(), true);
        } catch (Exception e) {
            // [注释] 我们使用 Log.e 替代 printStackTrace
            Log.e("MenuHelper", "Error forcing icons to show", e);
        }

        // 5. [注释] 显示弹窗
        popup.show();
    }
}
