// File: app/src/main/java/com/example/app_selfcare/Data/Model/Food.java
package com.example.app_selfcare.Data.Model;

import java.util.List;

public class Food {
    private String id;                    // dùng String để tiện với Firebase
    private String name;
    private String description;
    private int calories;
    private int timeMinutes;
    private String difficulty;            // "Dễ", "Trung bình", "Khó"
    private String mealType;              // "BREAKFAST", "LUNCH", "DINNER", "SNACK"
    private List<Ingredient> ingredients;
    private List<Step> steps;
    private String imageUrl;              // nếu up ảnh lên Firebase Storage

    // Constructor rỗng bắt buộc khi dùng Firebase
    public Food() {}

    public Food(String name, String description, int calories, int timeMinutes,
                String difficulty, String mealType, List<Ingredient> ingredients,
                List<Step> steps) {
        this.name = name;
        this.description = description;
        this.calories = calories;
        this.timeMinutes = timeMinutes;
        this.difficulty = difficulty;
        this.mealType = mealType;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public int getTimeMinutes() { return timeMinutes; }
    public void setTimeMinutes(int timeMinutes) { this.timeMinutes = timeMinutes; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }

    public List<Step> getSteps() { return steps; }
    public void setSteps(List<Step> steps) { this.steps = steps; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}