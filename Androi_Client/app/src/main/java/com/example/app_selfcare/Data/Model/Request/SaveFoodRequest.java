package com.example.app_selfcare.Data.Model.Request;

public class SaveFoodRequest {
    private Long foodId;

    public SaveFoodRequest() {}

    public SaveFoodRequest(Long foodId) {
        this.foodId = foodId;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }
}