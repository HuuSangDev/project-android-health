package com.example.app_selfcare.Data.Model.Response;

public class ExerciseResponse {
    private Long exerciseId;
    private String exerciseName;
    private Double caloriesPerMinute;
    private String description;
    private String instructions;
    private String difficultyLevel;
    private String equipmentNeeded;
    private String muscleGroups;
    private String imageUrl;
    private String videoUrl;
    private CategoryResponse category;

    public Long getExerciseId() { return exerciseId; }
    public String getExerciseName() { return exerciseName; }
    public Double getCaloriesPerMinute() { return caloriesPerMinute; }
    public String getDescription() { return description; }
    public String getInstructions() { return instructions; }
    public String getDifficultyLevel() { return difficultyLevel; }
    public String getEquipmentNeeded() { return equipmentNeeded; }
    public String getMuscleGroups() { return muscleGroups; }
    public String getImageUrl() { return imageUrl; }
    public String getVideoUrl() { return videoUrl; }
    public CategoryResponse getCategory() { return category; }
}

