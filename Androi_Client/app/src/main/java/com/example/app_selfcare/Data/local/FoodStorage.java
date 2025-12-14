package com.example.app_selfcare.Data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.app_selfcare.Data.Model.Food;
import com.example.app_selfcare.Data.Model.Ingredient;
import com.example.app_selfcare.Data.Model.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * Lưu tạm món ăn bằng SharedPreferences để hiển thị trong danh sách.
 * Không phải giải pháp prod, chỉ để demo lưu local.
 */
public class FoodStorage {
    private static final String PREFS_NAME = "FOOD_DATA";
    private static final String KEY_FOOD_LIST = "food_list";
    private static final String ITEM_SEPARATOR = ";;";
    private static final String FIELD_SEPARATOR = "\\|";

    public static List<Food> getFoods(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String raw = prefs.getString(KEY_FOOD_LIST, "");

        if (TextUtils.isEmpty(raw)) {
            List<Food> seed = seed();
            saveAll(context, seed);
            return seed;
        }
        return parse(raw);
    }

    public static void addFood(Context context, Food food) {
        if (food == null) return;
        if (TextUtils.isEmpty(food.getId())) {
            food.setId("food_" + System.currentTimeMillis());
        }
        List<Food> list = getFoods(context);
        list.add(food);
        saveAll(context, list);
    }

    private static void saveAll(Context context, List<Food> list) {
        StringBuilder sb = new StringBuilder();
        for (Food f : list) {
            sb.append(safe(f.getId())).append("|")
                    .append(safe(f.getName())).append("|")
                    .append(safe(f.getDescription())).append("|")
                    .append(f.getCalories()).append("|")
                    .append(f.getTimeMinutes()).append("|")
                    .append(safe(f.getDifficulty()))
                    .append(ITEM_SEPARATOR);
        }
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_FOOD_LIST, sb.toString()).apply();
    }

    private static List<Food> parse(String raw) {
        List<Food> list = new ArrayList<>();
        if (TextUtils.isEmpty(raw)) return list;
        String[] items = raw.split(ITEM_SEPARATOR);
        for (String item : items) {
            if (TextUtils.isEmpty(item)) continue;
            String[] parts = item.split(FIELD_SEPARATOR);
            if (parts.length < 6) continue;
            String id = parts[0];
            String name = parts[1];
            String desc = parts[2];
            int calories = parseInt(parts[3], 0);
            int time = parseInt(parts[4], 0);
            String diff = parts[5];

            // Ingredients/steps demo rỗng; có thể bổ sung nếu cần
            List<Ingredient> ing = new ArrayList<>();
            List<Step> steps = new ArrayList<>();

            Food f = new Food(name, desc, calories, time, diff, "LUNCH", ing, steps);
            f.setId(id);
            list.add(f);
        }
        return list;
    }

    private static List<Food> seed() {
        List<Food> list = new ArrayList<>();
        list.add(new Food("Salad rau củ", "Healthy và ngon", 150, 10, "Dễ", "LUNCH", new ArrayList<>(), new ArrayList<>()));
        list.add(new Food("Cơm gạo lứt", "Ít tinh bột xấu", 200, 25, "Trung bình", "LUNCH", new ArrayList<>(), new ArrayList<>()));
        return list;
    }

    private static int parseInt(String v, int fb) {
        try {
            return Integer.parseInt(v);
        } catch (Exception e) {
            return fb;
        }
    }

    private static String safe(String v) {
        if (v == null) return "";
        return v.replace("|", "/").replace(ITEM_SEPARATOR, "");
    }
}

