package com.example.app_selfcare.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

/**
 * LocaleManager - Quản lý ngôn ngữ cho toàn bộ ứng dụng
 */
public class LocaleManager {

    private static final String PREFS_NAME = "APP_SETTINGS";
    private static final String KEY_LANGUAGE = "app_language";
    
    public static final String LANGUAGE_VIETNAMESE = "vi";
    public static final String LANGUAGE_ENGLISH = "en";

    private final SharedPreferences prefs;
    private final Context context;

    public LocaleManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Lấy ngôn ngữ hiện tại đã lưu
     */
    public String getCurrentLanguage() {
        return prefs.getString(KEY_LANGUAGE, LANGUAGE_VIETNAMESE);
    }

    /**
     * Kiểm tra có đang dùng tiếng Anh không
     */
    public boolean isEnglish() {
        return LANGUAGE_ENGLISH.equals(getCurrentLanguage());
    }

    /**
     * Lưu ngôn ngữ đã chọn
     */
    public void setLanguage(String languageCode) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply();
    }

    /**
     * Chuyển đổi ngôn ngữ (toggle)
     */
    public String toggleLanguage() {
        String currentLang = getCurrentLanguage();
        String newLang = LANGUAGE_VIETNAMESE.equals(currentLang) ? LANGUAGE_ENGLISH : LANGUAGE_VIETNAMESE;
        setLanguage(newLang);
        return newLang;
    }

    /**
     * Áp dụng ngôn ngữ cho Context
     */
    public Context applyLanguage(Context context) {
        String language = getCurrentLanguage();
        return updateResources(context, language);
    }

    /**
     * Cập nhật Resources với ngôn ngữ mới
     */
    private Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = new Configuration(resources.getConfiguration());
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }

    /**
     * Lấy tên ngôn ngữ để hiển thị
     */
    public String getLanguageDisplayName() {
        return isEnglish() ? "English" : "Tiếng Việt";
    }

    /**
     * Lấy tên ngôn ngữ ngắn gọn
     */
    public String getLanguageShortName() {
        return isEnglish() ? "EN" : "VI";
    }
}
