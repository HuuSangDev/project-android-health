package com.example.app_selfcare.Data.Model;

public class Exercise {
    public int id;
    public String name;
    public String description;
    public int durationMinutes;
    public int caloriesBurned;
    // từ API mới
    public double caloriesPerMinute;
    public String difficulty;
    public String categoryId;
    public int imageResId;
    public String imageUrl;

    public Exercise() {}

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

    // Getters & Setters (giống trên)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public int getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(int caloriesBurned) { this.caloriesBurned = caloriesBurned; }
    public double getCaloriesPerMinute() { return caloriesPerMinute; }
    public void setCaloriesPerMinute(double caloriesPerMinute) { this.caloriesPerMinute = caloriesPerMinute; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}