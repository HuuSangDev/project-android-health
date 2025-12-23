package com.example.app_selfcare.Data.Model.Response;

public class SavedFoodResponse {
    private Long savedFoodId;
    private Long foodId;
    private String foodName;
    private String imageUrl;
    private Double caloriesPer100g;
    private Integer prepTime;
    private Integer cookTime;
    private String mealType;
    private String difficultyLevel;
    private String savedAt;

    public SavedFoodResponse() {}

    // Getters and Setters
    public Long getSavedFoodId() {
        return savedFoodId;
    }

    public void setSavedFoodId(Long savedFoodId) {
        this.savedFoodId = savedFoodId;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getCaloriesPer100g() {
        return caloriesPer100g;
    }

    public void setCaloriesPer100g(Double caloriesPer100g) {
        this.caloriesPer100g = caloriesPer100g;
    }

    public Integer getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(Integer prepTime) {
        this.prepTime = prepTime;
    }

    public Integer getCookTime() {
        return cookTime;
    }

    public void setCookTime(Integer cookTime) {
        this.cookTime = cookTime;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(String savedAt) {
        this.savedAt = savedAt;
    }

    // Helper methods
    public int getTotalTime() {
        return (prepTime != null ? prepTime : 0) + (cookTime != null ? cookTime : 0);
    }

    public String getFormattedCalories() {
        return caloriesPer100g != null ? String.format("%.0f cal", caloriesPer100g) : "N/A";
    }
}