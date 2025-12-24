package com.example.app_selfcare.Data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.app_selfcare.Data.Model.Exercise;
import com.example.app_selfcare.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Small helper to persist exercises locally using SharedPreferences.
 * This keeps the UI working without a real backend/DB.
 */
public class ExerciseStorage {

    private static final String PREFS_NAME = "EXERCISE_DATA";
    private static final String KEY_EXERCISE_LIST = "exercise_list";
    private static final String ITEM_SEPARATOR = ";;";
    private static final String FIELD_SEPARATOR = "\\|";

    public static List<Exercise> getExercises(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String raw = prefs.getString(KEY_EXERCISE_LIST, "");

        if (TextUtils.isEmpty(raw)) {
            List<Exercise> seed = getSeedData();
            saveAll(context, seed);
            return seed;
        }

        return parse(raw);
    }

    // Method để force reload dữ liệu mẫu mới (bỏ qua dữ liệu cũ)
    public static List<Exercise> getExercisesWithFreshSeed(Context context) {
        List<Exercise> seed = getSeedData();
        saveAll(context, seed);
        return seed;
    }

    // Method để reset và tải lại dữ liệu mẫu mới
    public static void resetToSeedData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply(); // Xóa dữ liệu cũ
        
        List<Exercise> seed = getSeedData();
        saveAll(context, seed);
    }

    // Method để xóa tất cả dữ liệu
    public static void clearAllData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    public static void addExercise(Context context, Exercise exercise) {
        if (exercise == null) return;

        if (exercise.getId() == 0) {
            exercise.setId((int) (System.currentTimeMillis() & Integer.MAX_VALUE));
        }
        if (exercise.getImageResId() == 0) {
            exercise.setImageResId(R.drawable.ic_active);
        }
        if (TextUtils.isEmpty(exercise.getCategoryId())) {
            exercise.setCategoryId("default");
        }

        List<Exercise> current = getExercises(context);
        current.add(exercise);
        saveAll(context, current);
    }

    public static void deleteExercise(Context context, int id) {
        List<Exercise> current = getExercises(context);
        boolean changed = false;
        List<Exercise> updated = new ArrayList<>();

        for (Exercise item : current) {
            if (item.getId() == id) {
                changed = true;
                continue;
            }
            updated.add(item);
        }

        if (changed) {
            saveAll(context, updated);
        }
    }

    private static void saveAll(Context context, List<Exercise> list) {
        StringBuilder builder = new StringBuilder();
        for (Exercise e : list) {
            builder.append(e.getId()).append("|")
                    .append(safe(e.getName())).append("|")
                    .append(safe(e.getDescription())).append("|")
                    .append(e.getDurationMinutes()).append("|")
                    .append(e.getCaloriesBurned()).append("|")
                    .append(safe(e.getDifficulty())).append("|")
                    .append(safe(e.getCategoryId())).append(ITEM_SEPARATOR);
        }

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_EXERCISE_LIST, builder.toString()).apply();
    }

    private static List<Exercise> parse(String raw) {
        List<Exercise> result = new ArrayList<>();
        if (TextUtils.isEmpty(raw)) return result;

        String[] items = raw.split(ITEM_SEPARATOR);
        for (String item : items) {
            if (TextUtils.isEmpty(item)) continue;

            String[] parts = item.split(FIELD_SEPARATOR);
            if (parts.length < 7) continue;

            Exercise exercise = new Exercise();
            exercise.setId(parseInt(parts[0], 0));
            exercise.setName(parts[1]);
            exercise.setDescription(parts[2]);
            exercise.setDurationMinutes(parseInt(parts[3], 0));
            exercise.setCaloriesBurned(parseInt(parts[4], 0));
            exercise.setDifficulty(parts[5]);
            exercise.setCategoryId(parts[6]);
            exercise.setImageResId(R.drawable.ic_active);

            result.add(exercise);
        }
        return result;
    }

    private static List<Exercise> getSeedData() {
        List<Exercise> list = new ArrayList<>();

        // Dữ liệu mẫu hiện tại
        list.add(new Exercise(1, "Chống đẩy", "20 cái mỗi hiệp", 10, 80, "Dễ", "default", R.drawable.ic_active));
        list.add(new Exercise(2, "Squat", "30 lần tăng sức mạnh chân", 12, 90, "Trung bình", "default", R.drawable.ic_active));
        list.add(new Exercise(3, "Plank", "Giữ plank 60 giây", 1, 50, "Khó", "default", R.drawable.ic_active));

        list.add(new Exercise(4, "Burpees", "Bài tập toàn thân hiệu quả", 15, 120, "Khó", "default", R.drawable.ic_active));
        list.add(new Exercise(5, "Jumping Jacks", "Nhảy tại chỗ 50 lần", 8, 60, "Dễ", "default", R.drawable.ic_active));
        list.add(new Exercise(6, "Mountain Climbers", "Leo núi tại chỗ 30 giây", 10, 85, "Trung bình", "default", R.drawable.ic_active));
        list.add(new Exercise(7, "Lunges", "Chùng chân 20 lần mỗi bên", 12, 75, "Trung bình", "default", R.drawable.ic_active));
        list.add(new Exercise(8, "High Knees", "Nâng đầu gối cao 30 giây", 8, 70, "Dễ", "default", R.drawable.ic_active));
        list.add(new Exercise(9, "Russian Twists", "Xoay người 25 lần mỗi bên", 10, 65, "Trung bình", "default", R.drawable.ic_active));
        list.add(new Exercise(10, "Wall Sit", "Ngồi tựa tường 45 giây", 5, 40, "Dễ", "default", R.drawable.ic_active));

        return list;
    }

    private static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static String safe(String value) {
        if (value == null) return "";
        return value.replace("|", "/").replace(ITEM_SEPARATOR, "");
    }
}
