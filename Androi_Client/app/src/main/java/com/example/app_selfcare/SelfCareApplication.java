package com.example.app_selfcare;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.example.app_selfcare.utils.LocaleManager;
import com.example.app_selfcare.utils.ThemeManager;

/**
 * Application class để khởi tạo theme và ngôn ngữ khi app bắt đầu
 */
public class SelfCareApplication extends Application {

    private static final String TAG = "SelfCareApplication";
    private static SelfCareApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        try {
            // Áp dụng theme đã lưu khi app khởi động
            ThemeManager themeManager = new ThemeManager(this);
            themeManager.applySavedTheme();
        } catch (Exception e) {
            Log.e(TAG, "Error applying theme: " + e.getMessage());
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        // Áp dụng ngôn ngữ đã lưu cho toàn bộ Application
        LocaleManager localeManager = new LocaleManager(base);
        super.attachBaseContext(localeManager.applyLanguage(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Đảm bảo ngôn ngữ được giữ khi configuration thay đổi
        LocaleManager localeManager = new LocaleManager(this);
        localeManager.applyLanguage(this);
    }

    public static SelfCareApplication getInstance() {
        return instance;
    }
}
