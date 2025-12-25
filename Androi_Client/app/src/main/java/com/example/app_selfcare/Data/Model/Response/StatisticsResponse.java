package com.example.app_selfcare.Data.Model.Response;

import com.google.gson.annotations.SerializedName;

public class StatisticsResponse {
    @SerializedName("totalUsers")
    private long totalUsers;

    @SerializedName("activeUsers")
    private long activeUsers;

    @SerializedName("totalExercises")
    private long totalExercises;

    @SerializedName("totalFoods")
    private long totalFoods;

    @SerializedName("totalNotifications")
    private long totalNotifications;

    @SerializedName("unreadNotifications")
    private long unreadNotifications;

    // Getters
    public long getTotalUsers() { return totalUsers; }
    public long getActiveUsers() { return activeUsers; }
    public long getTotalExercises() { return totalExercises; }
    public long getTotalFoods() { return totalFoods; }
    public long getTotalNotifications() { return totalNotifications; }
    public long getUnreadNotifications() { return unreadNotifications; }

    // Setters
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }
    public void setTotalExercises(long totalExercises) { this.totalExercises = totalExercises; }
    public void setTotalFoods(long totalFoods) { this.totalFoods = totalFoods; }
    public void setTotalNotifications(long totalNotifications) { this.totalNotifications = totalNotifications; }
    public void setUnreadNotifications(long unreadNotifications) { this.unreadNotifications = unreadNotifications; }
}