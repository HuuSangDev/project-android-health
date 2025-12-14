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

    public static void addExercise(Context context, Exercise exercise) {
        if (exercise == null) return;

        if (exercise.id == 0) {
            exercise.id = (int) (System.currentTimeMillis() & Integer.MAX_VALUE);
        }
        if (exercise.imageResId == 0) {
            exercise.imageResId = R.drawable.ic_active;
        }
        if (TextUtils.isEmpty(exercise.categoryId)) {
            exercise.categoryId = "default";
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
            if (item.id == id) {
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
            builder.append(e.id).append("|")
                    .append(safe(e.name)).append("|")
                    .append(safe(e.description)).append("|")
                    .append(e.durationMinutes).append("|")
                    .append(e.caloriesBurned).append("|")
                    .append(safe(e.difficulty)).append("|")
                    .append(safe(e.categoryId)).append(ITEM_SEPARATOR);
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
            exercise.id = parseInt(parts[0], 0);
            exercise.name = parts[1];
            exercise.description = parts[2];
            exercise.durationMinutes = parseInt(parts[3], 0);
            exercise.caloriesBurned = parseInt(parts[4], 0);
            exercise.difficulty = parts[5];
            exercise.categoryId = parts[6];
            exercise.imageResId = R.drawable.ic_active;

            result.add(exercise);
        }
        return result;
    }

    private static List<Exercise> getSeedData() {
        List<Exercise> list = new ArrayList<>();

        list.add(new Exercise(1, "Chống đẩy", "20 cái mỗi hiệp", 10, 80, "Dễ", "default", R.drawable.ic_active));
        list.add(new Exercise(2, "Squat", "30 lần tăng sức mạnh chân", 12, 90, "Trung bình", "default", R.drawable.ic_active));
        list.add(new Exercise(3, "Plank", "Giữ plank 60 giây", 1, 50, "Khó", "default", R.drawable.ic_active));

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
