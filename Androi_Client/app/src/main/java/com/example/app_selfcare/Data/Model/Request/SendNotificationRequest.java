package com.example.app_selfcare.Data.Model.Request;

import com.google.gson.annotations.SerializedName;

public class SendNotificationRequest {
    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("type")
    private String type; // FOOD hoặc EXERCISE

    @SerializedName("targetId")
    private Long targetId;

    @SerializedName("goal")
    private String goal; // WEIGHT_LOSS, MUSCLE_GAIN, MAINTAIN_WEIGHT

    public SendNotificationRequest() {}

    public SendNotificationRequest(String title, String message, String type, Long targetId, String goal) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.targetId = targetId;
        this.goal = goal;
    }

    // Getters
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public Long getTargetId() { return targetId; }
    public String getGoal() { return goal; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setType(String type) { this.type = type; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public void setGoal(String goal) { this.goal = goal; }
}