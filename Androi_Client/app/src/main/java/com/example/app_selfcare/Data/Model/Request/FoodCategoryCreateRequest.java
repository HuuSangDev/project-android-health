package com.example.app_selfcare.Data.Model.Request;

import com.google.gson.annotations.SerializedName;

public class FoodCategoryCreateRequest {

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("description")
    private String description;

    @SerializedName("iconUrl")
    private String iconUrl;

    public FoodCategoryCreateRequest() {}

    public FoodCategoryCreateRequest(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
        this.iconUrl = null;
    }

    public FoodCategoryCreateRequest(String categoryName, String description, String iconUrl) {
        this.categoryName = categoryName;
        this.description = description;
        this.iconUrl = (iconUrl != null && !iconUrl.isEmpty()) ? iconUrl : null;
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
