package com.example.app_selfcare.Data.Model;

public class Category {
    public int id;
    public String name;
    public String type; // "food" hoặc "exercise"
    public int iconResId;

    public Category() {}

    public Category(int id, String name, String type, int iconResId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.iconResId = iconResId;
    }

    // Getters & Setters...
}