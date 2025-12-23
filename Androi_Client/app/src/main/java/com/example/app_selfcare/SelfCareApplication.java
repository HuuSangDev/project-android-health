package com.example.app_selfcare;

import android.app.Application;
import android.util.Log;

import com.example.app_selfcare.utils.ThemeManager;

/**
 * Application class để khởi tạo theme khi app bắt đầu
 */
public class SelfCareApplication extends Application {

    private static final String TAG = "SelfCareApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        
        try {
            // Áp dụng theme đã lưu khi app khởi động
            ThemeManager themeManager = new ThemeManager(this);
            themeManager.applySavedTheme();
        } catch (Exception e) {
            Log.e(TAG, "Error applying theme: " + e.getMessage());
        }
    }
}
