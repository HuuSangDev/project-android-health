package com.example.app_selfcare.Data.Model.Request;

import com.google.gson.annotations.SerializedName;

public class FoodCategoryCreateRequest {

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("description")
    private String description;

    public FoodCategoryCreateRequest() {}

    public FoodCategoryCreateRequest(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
