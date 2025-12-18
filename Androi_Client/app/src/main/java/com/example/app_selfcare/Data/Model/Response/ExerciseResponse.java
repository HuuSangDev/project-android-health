package com.example.app_selfcare.Data.Model.Response;

import com.example.app_selfcare.Data.Model.Exercise;
import com.google.gson.annotations.SerializedName;

/**
 * Model ánh xạ trực tiếp JSON trả về từ API /app/exercises/all.
 */
public class ExerciseResponse {

    @SerializedName("exerciseId")
    private int exerciseId;

    @SerializedName("exerciseName")
    private String exerciseName;

    @SerializedName("caloriesPerMinute")
    private double caloriesPerMinute;

    @SerializedName("description")
    private String description;

    @SerializedName("instructions")
    private String instructions;

    @SerializedName("difficultyLevel")
    private String difficultyLevel;

    @SerializedName("equipmentNeeded")
    private String equipmentNeeded;

    @SerializedName("muscleGroups")
    private String muscleGroups;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("videoUrl")
    private String videoUrl;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("category")
    private CategoryResponse category;

    public int getExerciseId() {
        return exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public double getCaloriesPerMinute() {
        return caloriesPerMinute;
    }

    public String getDescription() {
        return description;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public String getEquipmentNeeded() {
        return equipmentNeeded;
    }

    public String getMuscleGroups() {
        return muscleGroups;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public CategoryResponse getCategory() {
        return category;
    }

    /**
     * Chuyển dữ liệu response sang model Exercise dùng trong UI / local.
     */
    public Exercise toExercise() {
        Exercise e = new Exercise();
        e.setId(exerciseId);
        e.setName(exerciseName);
        e.setDescription(description != null ? description : "");
        e.setCaloriesPerMinute(caloriesPerMinute);
        // caloriesBurned có thể tính theo 1 phút để hiển thị ở chỗ khác nếu cần
        e.setCaloriesBurned((int) Math.round(caloriesPerMinute));
        e.setDifficulty(mapDifficulty(difficultyLevel));
        e.setCategoryId(category != null ? category.getCategoryName() : "");
        e.setImageUrl(imageUrl);
        return e;
    }

    private String mapDifficulty(String level) {
        if (level == null) return "Dễ";
        switch (level.toUpperCase()) {
            case "BEGINNER":
                return "Dễ";
            case "INTERMEDIATE":
                return "Trung bình";
            case "ADVANCED":
                return "Khó";
            default:
                return level;
        }
    }
}


