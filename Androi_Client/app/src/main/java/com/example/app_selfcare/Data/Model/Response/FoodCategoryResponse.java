package com.example.app_selfcare.Data.Model.Response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FoodCategoryResponse implements Serializable {

    @SerializedName("categoryId")
    private long categoryId;

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("description")
    private String description;

    public FoodCategoryResponse() {}

    public FoodCategoryResponse(long categoryId, String categoryName, String description) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
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

    @Override
    public String toString() {
        return categoryName; // Để hiển thị trong Spinner
    }
}
