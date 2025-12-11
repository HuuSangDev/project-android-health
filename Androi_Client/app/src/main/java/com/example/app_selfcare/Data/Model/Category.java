// File: app/src/main/java/com/example/app_selfcare/Data/Model/Category.java
package com.example.app_selfcare.Data.Model;

public class Category {
    public int id;
    public String name;
    public String type;
    public int iconResId;

    public Category() {}

    public Category(int id, String name, String type, int iconResId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.iconResId = iconResId;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public int getIconResId() { return iconResId; }

    // Setters – CHỈNH LẠI TÊN ĐÚNG
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; } // ĐÚNG TÊN
}