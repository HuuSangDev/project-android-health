package com.example.app_selfcare.Data.Model;

public class Step {
    private String number;
    private String description;

    public Step(String number, String description) {
        this.number = number;
        this.description = description;
    }

    // Getters
    public String getNumber() { return number; }
    public String getDescription() { return description; }

    // (Tùy chọn) Setters nếu cần chỉnh sửa sau
    public void setNumber(String number) { this.number = number; }
    public void setDescription(String description) { this.description = description; }
}