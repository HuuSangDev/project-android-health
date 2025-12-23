package com.example.app_selfcare.utils;

import android.content.Context;

import com.example.app_selfcare.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class để dịch các text phổ biến từ server
 * Dùng cho các text như: độ khó, thời gian, loại bài tập, v.v.
 */
public class TranslationHelper {

    private final Context context;
    private final LocaleManager localeManager;
    
    // Mapping Vietnamese -> English
    private static final Map<String, String> VI_TO_EN = new HashMap<>();
    
    static {
        // Difficulty levels
        VI_TO_EN.put("Dễ", "Easy");
        VI_TO_EN.put("Trung bình", "Medium");
        VI_TO_EN.put("Khó", "Hard");
        
        // Time units
        VI_TO_EN.put("phút", "min");
        VI_TO_EN.put("giờ", "hour");
        VI_TO_EN.put("giây", "sec");
        
        // Exercise types
        VI_TO_EN.put("Cardio", "Cardio");
        VI_TO_EN.put("Sức mạnh", "Strength");
        VI_TO_EN.put("Yoga", "Yoga");
        VI_TO_EN.put("Giãn cơ", "Stretching");
        
        // Meal types
        VI_TO_EN.put("Sáng", "Breakfast");
        VI_TO_EN.put("Trưa", "Lunch");
        VI_TO_EN.put("Tối", "Dinner");
        VI_TO_EN.put("Ăn vặt", "Snack");
        
        // Health goals
        VI_TO_EN.put("Tăng cân", "Gain weight");
        VI_TO_EN.put("Giảm cân", "Lose weight");
        VI_TO_EN.put("Giữ cân", "Maintain weight");
        
        // Common words
        VI_TO_EN.put("bài tập", "exercises");
        VI_TO_EN.put("kcal", "kcal");
    }

    public TranslationHelper(Context context) {
        this.context = context;
        this.localeManager = new LocaleManager(context);
    }

    /**
     * Dịch text nếu đang ở chế độ tiếng Anh
     */
    public String translate(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        // Nếu đang dùng tiếng Việt, không cần dịch
        if (!localeManager.isEnglish()) {
            return text;
        }
        
        // Kiểm tra xem có trong mapping không
        if (VI_TO_EN.containsKey(text)) {
            return VI_TO_EN.get(text);
        }
        
        // Thử dịch từng phần của text
        String result = text;
        for (Map.Entry<String, String> entry : VI_TO_EN.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        
        return result;
    }

    /**
     * Dịch độ khó
     */
    public String translateDifficulty(String difficulty) {
        if (difficulty == null) return "";
        
        if (!localeManager.isEnglish()) {
            return difficulty;
        }
        
        switch (difficulty.toLowerCase()) {
            case "dễ":
            case "easy":
                return "Easy";
            case "trung bình":
            case "medium":
                return "Medium";
            case "khó":
            case "hard":
                return "Hard";
            default:
                return difficulty;
        }
    }

    /**
     * Dịch loại bữa ăn
     */
    public String translateMealType(String mealType) {
        if (mealType == null) return "";
        
        if (!localeManager.isEnglish()) {
            return mealType;
        }
        
        switch (mealType.toUpperCase()) {
            case "BREAKFAST":
            case "SÁNG":
                return "Breakfast";
            case "LUNCH":
            case "TRƯA":
                return "Lunch";
            case "DINNER":
            case "TỐI":
                return "Dinner";
            default:
                return mealType;
        }
    }

    /**
     * Format thời gian với đơn vị đã dịch
     */
    public String formatTime(int minutes) {
        if (localeManager.isEnglish()) {
            return minutes + " min";
        } else {
            return minutes + " phút";
        }
    }

    /**
     * Format số bài tập
     */
    public String formatExerciseCount(int count) {
        if (localeManager.isEnglish()) {
            return count + " exercises";
        } else {
            return count + " bài tập";
        }
    }
}
