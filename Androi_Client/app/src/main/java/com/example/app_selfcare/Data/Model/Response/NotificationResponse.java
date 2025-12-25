package com.example.app_selfcare.Data.Model.Response;

import com.google.gson.annotations.SerializedName;

public class NotificationResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("type")
    private String type; // FOOD hoặc EXERCISE

    @SerializedName("targetId")
    private Long targetId;

    @SerializedName("goal")
    private String goal;

    @SerializedName("isRead")
    private boolean isRead;

    @SerializedName("createdAt")
    private String createdAt;

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public Long getTargetId() { return targetId; }
    public String getGoal() { return goal; }
    public boolean isRead() { return isRead; }
    public String getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setType(String type) { this.type = type; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public void setGoal(String goal) { this.goal = goal; }
    public void setRead(boolean read) { isRead = read; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}