package com.example.app_selfcare.Data.Model.Request;

import com.google.gson.annotations.SerializedName;

public class ExerciseCategoryCreateRequest {

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("description")
    private String description;

    @SerializedName("iconUrl")
    private String iconUrl;

    public ExerciseCategoryCreateRequest(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
        this.iconUrl = null;
    }

    public ExerciseCategoryCreateRequest(String categoryName, String description, String iconUrl) {
        this.categoryName = categoryName;
        this.description = description;
        this.iconUrl = iconUrl;
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

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
