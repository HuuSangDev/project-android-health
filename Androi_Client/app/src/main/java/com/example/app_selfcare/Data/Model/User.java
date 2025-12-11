package com.example.app_selfcare.Data.Model;

public class User {
    public String uid, name, email, gender, birthdate;
    public int height, weight, goalWeight;
    public long joinedDate;
    public char[] heightCm;
    public Object weightKg;
    public Object goalWeightKg;

    public User(String uid, String name, String email, String gender, String birthdate, int height, int weight, int goalWeight) {
        this.uid = uid; this.name = name; this.email = email; this.gender = gender;
        this.birthdate = birthdate; this.height = height; this.weight = weight;
        this.goalWeight = goalWeight; this.joinedDate = System.currentTimeMillis();
    }
}