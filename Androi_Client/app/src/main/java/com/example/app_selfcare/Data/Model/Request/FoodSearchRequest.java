package com.example.app_selfcare.Data.Model.Request;

import com.google.gson.annotations.SerializedName;

public class FoodSearchRequest {

    @SerializedName("keyword")
    private String keyword;

    @SerializedName("mealType")
    private String mealType;

    @SerializedName("difficultyLevel")
    private String difficultyLevel;

    @SerializedName("minCalories")
    private Double minCalories;

    @SerializedName("maxCalories")
    private Double maxCalories;

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("categoryId")
    private Long categoryId;

    @SerializedName("page")
    private Integer page = 0;

    @SerializedName("size")
    private Integer size = 50;

    @SerializedName("sortBy")
    private String sortBy = "createdAt";

    @SerializedName("sortDir")
    private String sortDir = "desc";

    public FoodSearchRequest() {}

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private FoodSearchRequest request = new FoodSearchRequest();

        public Builder keyword(String keyword) {
            request.keyword = keyword;
            return this;
        }

        public Builder categoryId(Long categoryId) {
            request.categoryId = categoryId;
            return this;
        }

        public Builder mealType(String mealType) {
            request.mealType = mealType;
            return this;
        }

        public Builder page(int page) {
            request.page = page;
            return this;
        }

        public Builder size(int size) {
            request.size = size;
            return this;
        }

        public FoodSearchRequest build() {
            return request;
        }
    }

    // Getters and Setters
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }
    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public Double getMinCalories() { return minCalories; }
    public void setMinCalories(Double minCalories) { this.minCalories = minCalories; }
    public Double getMaxCalories() { return maxCalories; }
    public void setMaxCalories(Double maxCalories) { this.maxCalories = maxCalories; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
}
