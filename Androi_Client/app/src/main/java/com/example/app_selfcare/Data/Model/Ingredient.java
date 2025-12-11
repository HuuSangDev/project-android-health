package com.example.app_selfcare.Data.Model;

public class Ingredient {
    public String name;
    public String amount;
    public String icon; // emoji hoặc tên icon

    public Ingredient() {}

    public Ingredient(String icon, String name, String amount) {
        this.icon = icon;
        this.name = name;
        this.amount = amount;
    }

    // Getters & Setters
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
}