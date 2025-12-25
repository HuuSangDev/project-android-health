package com.example.app_selfcare.Data.Model.Response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FoodResponse implements Serializable {
    @SerializedName("foodId")
    private int foodId;

    @SerializedName("foodName")
    private String foodName;

    @SerializedName("caloriesPer100g")
    private double caloriesPer100g;

    @SerializedName("proteinPer100g")
    private double proteinPer100g;

    @SerializedName("fatPer100g")
    private double fatPer100g;

    @SerializedName("fiberPer100g")
    private double fiberPer100g;

    @SerializedName("sugarPer100g")
    private double sugarPer100g;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("instructions")
    private String instructions;

    @SerializedName("prepTime")
    private int prepTime;

    @SerializedName("cookTime")
    private int cookTime;

    @SerializedName("servings")
    private int servings;

    @SerializedName("difficultyLevel")
    private String difficultyLevel;

    @SerializedName("mealType")
    private String mealType;

    @SerializedName("categoryResponse")
    private FoodCategoryResponse categoryResponse;

    @SerializedName("goal")
    private String goal;

    // Getters and Setters
    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getCaloriesPer100g() {
        return caloriesPer100g;
    }

    public void setCaloriesPer100g(double caloriesPer100g) {
        this.caloriesPer100g = caloriesPer100g;
    }

    public double getProteinPer100g() {
        return proteinPer100g;
    }

    public void setProteinPer100g(double proteinPer100g) {
        this.proteinPer100g = proteinPer100g;
    }

    public double getFatPer100g() {
        return fatPer100g;
    }

    public void setFatPer100g(double fatPer100g) {
        this.fatPer100g = fatPer100g;
    }

    public double getFiberPer100g() {
        return fiberPer100g;
    }

    public void setFiberPer100g(double fiberPer100g) {
        this.fiberPer100g = fiberPer100g;
    }

    public double getSugarPer100g() {
        return sugarPer100g;
    }

    public void setSugarPer100g(double sugarPer100g) {
        this.sugarPer100g = sugarPer100g;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public FoodCategoryResponse getCategoryResponse() {
        return categoryResponse;
    }

    public void setCategoryResponse(FoodCategoryResponse categoryResponse) {
        this.categoryResponse = categoryResponse;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    // Helper method to convert to Food model (if needed)
    public com.example.app_selfcare.Data.Model.Food toFood() {
        com.example.app_selfcare.Data.Model.Food food = new com.example.app_selfcare.Data.Model.Food();
        food.setId(String.valueOf(foodId));
        food.setName(foodName);
        food.setDescription(instructions != null ? instructions : "");
        food.setCalories((int) caloriesPer100g);
        food.setTimeMinutes(prepTime + cookTime);
        food.setDifficulty(convertDifficulty(difficultyLevel));
        food.setMealType(mealType);
        food.setImageUrl(imageUrl);
        return food;
    }

    private String convertDifficulty(String difficultyLevel) {
        if (difficultyLevel == null) return "Dễ";
        switch (difficultyLevel.toUpperCase()) {
            case "EASY":
                return "Dễ";
            case "MEDIUM":
                return "Trung bình";
            case "HARD":
                return "Khó";
            default:
                return difficultyLevel;
        }
    }
}

