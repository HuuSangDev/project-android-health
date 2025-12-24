package com.example.app_selfcare.Data.Model.Request;

public class CreateDailyLogRequest {
    private Double currentWeight;
    private Double height;
    private String notes;

    public CreateDailyLogRequest() {}

    public CreateDailyLogRequest(Double currentWeight, Double height, String notes) {
        this.currentWeight = currentWeight;
        this.height = height;
        this.notes = notes;
    }

    // Getters and Setters
    public Double getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(Double currentWeight) {
        this.currentWeight = currentWeight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}