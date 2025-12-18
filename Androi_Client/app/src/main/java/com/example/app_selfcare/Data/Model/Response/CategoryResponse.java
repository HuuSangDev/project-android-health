package com.example.app_selfcare.Data.Model.Response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CategoryResponse implements Serializable {
    @SerializedName("categoryId")
    private int categoryId;

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("description")
    private String description;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
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
}

