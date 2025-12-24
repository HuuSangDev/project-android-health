package com.example.app_selfcare.Data.Model;

public class Exercise {
    // Core fields (used by both API and admin)
    private int id;
    private String name;
    private String description;
    
    // API-specific fields
    private String instructions;
    private double caloriesPerMinute;
    private String difficultyLevel;
    private String categoryName;
    private String imageUrl;

    // Admin/Local storage specific fields
    private int durationMinutes;
    private int caloriesBurned;
    private String difficulty;
    private String categoryId;
    private int imageResId;

    public Exercise() {}

    // Constructor for API responses
    public Exercise(int id, String name, String description, String instructions,
                    double caloriesPerMinute, String difficultyLevel, String categoryName, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.instructions = instructions;
        this.caloriesPerMinute = caloriesPerMinute;
        this.difficultyLevel = difficultyLevel;
        this.categoryName = categoryName;
        this.imageUrl = imageUrl;
    }

    // Constructor for local storage (admin)
    public Exercise(int id, String name, String description, int durationMinutes, 
                    int caloriesBurned, String difficulty, String categoryId, int imageResId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.difficulty = difficulty;
        this.categoryId = categoryId;
        this.imageResId = imageResId;
    }

    // Core Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // API Getters & Setters
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public double getCaloriesPerMinute() { return caloriesPerMinute; }
    public void setCaloriesPerMinute(double caloriesPerMinute) { this.caloriesPerMinute = caloriesPerMinute; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // Admin/Local storage Getters & Setters
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public int getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(int caloriesBurned) { this.caloriesBurned = caloriesBurned; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}