package com.example.app_selfcare.Data.Model.Response;

import com.example.app_selfcare.Data.Model.Exercise;
import com.google.gson.annotations.SerializedName;

/**
 * Model ánh xạ trực tiếp JSON trả về từ API /app/exercises/all.
 */
public class ExerciseResponse {

    @SerializedName("exerciseId")
    private Long exerciseId;

    @SerializedName("exerciseName")
    private String exerciseName;

    @SerializedName("caloriesPerMinute")
    private Double caloriesPerMinute;

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
    private ExerciseCategoryResponse category;

    @SerializedName("goal")
    private String goal;

    public Long getExerciseId() {
        return exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public Double getCaloriesPerMinute() {
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

    public ExerciseCategoryResponse getCategory() {
        return category;
    }

    public String getGoal() {
        return goal;
    }

    /**
     * Chuyển dữ liệu response sang model Exercise dùng trong UI / local.
     */
    public Exercise toExercise() {
        Exercise e = new Exercise();
        e.setId(exerciseId != null ? exerciseId.intValue() : 0);
        e.setName(exerciseName);
        e.setDescription(description != null ? description : "");
        e.setInstructions(instructions != null ? instructions : "");
        e.setCaloriesPerMinute(caloriesPerMinute);
        e.setDifficultyLevel(mapDifficulty(difficultyLevel));
        e.setCategoryName(category != null ? category.getCategoryName() : "");
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


