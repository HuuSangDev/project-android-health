package com.example.app_selfcare.Data.Model;

public class Notification {
    public String id;
    public String title;
    public String message;
    public long timestamp;
    public boolean isRead;

    public Notification() {}

    public Notification(String id, String title, String message) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }
}