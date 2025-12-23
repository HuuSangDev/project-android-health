package com.example.app_selfcare.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * ThemeManager - Quản lý chế độ Dark Mode cho toàn bộ ứng dụng
 */
public class ThemeManager {

    private static final String PREFS_NAME = "APP_SETTINGS";
    private static final String KEY_DARK_MODE = "dark_mode_enabled";

    private final SharedPreferences prefs;

    public ThemeManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Kiểm tra Dark Mode có đang bật không
     */
    public boolean isDarkModeEnabled() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    /**
     * Bật/tắt Dark Mode và lưu trạng thái
     */
    public void setDarkModeEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
        applyTheme(enabled);
    }

    /**
     * Áp dụng theme dựa trên trạng thái
     */
    public void applyTheme(boolean darkMode) {
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    /**
     * Áp dụng theme đã lưu (gọi khi khởi động app)
     */
    public void applySavedTheme() {
        applyTheme(isDarkModeEnabled());
    }

    /**
     * Toggle Dark Mode
     */
    public boolean toggleDarkMode() {
        boolean newState = !isDarkModeEnabled();
        setDarkModeEnabled(newState);
        return newState;
    }
}
