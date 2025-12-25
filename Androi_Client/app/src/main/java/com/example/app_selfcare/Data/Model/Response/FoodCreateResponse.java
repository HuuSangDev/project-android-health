package com.example.app_selfcare.Data.Model.Response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FoodCreateResponse implements Serializable {
    @SerializedName("foodId")
    private Long foodId;

    @SerializedName("foodName")
    private String foodName;

    @SerializedName("caloriesPer100g")
    private Double caloriesPer100g;

    @SerializedName("proteinPer100g")
    private Double proteinPer100g;

    @SerializedName("fatPer100g")
    private Double fatPer100g;

    @SerializedName("fiberPer100g")
    private Double fiberPer100g;

    @SerializedName("sugarPer100g")
    private Double sugarPer100g;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("instructions")
    private String instructions;

    @SerializedName("prepTime")
    private Integer prepTime;

    @SerializedName("cookTime")
    private Integer cookTime;

    @SerializedName("servings")
    private Integer servings;

    @SerializedName("difficultyLevel")
    private String difficultyLevel;

    @SerializedName("mealType")
    private String mealType;

    @SerializedName("categoryResponse")
    private FoodCategoryResponse categoryResponse;

    @SerializedName("goal")
    private String goal;

    // Getters and Setters
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

    public Double getCaloriesPer100g() {
        return caloriesPer100g;
    }

    public void setCaloriesPer100g(Double caloriesPer100g) {
        this.caloriesPer100g = caloriesPer100g;
    }

    public Double getProteinPer100g() {
        return proteinPer100g;
    }

    public void setProteinPer100g(Double proteinPer100g) {
        this.proteinPer100g = proteinPer100g;
    }

    public Double getFatPer100g() {
        return fatPer100g;
    }

    public void setFatPer100g(Double fatPer100g) {
        this.fatPer100g = fatPer100g;
    }

    public Double getFiberPer100g() {
        return fiberPer100g;
    }

    public void setFiberPer100g(Double fiberPer100g) {
        this.fiberPer100g = fiberPer100g;
    }

    public Double getSugarPer100g() {
        return sugarPer100g;
    }

    public void setSugarPer100g(Double sugarPer100g) {
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

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
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

    // Helper method to get food ID as int for SearchItem
    public int getFoodIdAsInt() {
        return foodId != null ? foodId.intValue() : 0;
    }
}