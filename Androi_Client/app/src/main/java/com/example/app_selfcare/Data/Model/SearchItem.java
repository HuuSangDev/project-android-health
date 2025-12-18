package com.example.app_selfcare.Data.Model;

public class SearchItem {

    public static final int TYPE_WORKOUT = 1;
    public static final int TYPE_FOOD = 2;

    private final int id;
    private final String name;
    private final int type;

    public SearchItem(int id, String name, int type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public String getTypeLabel() {
        return type == TYPE_WORKOUT ? "Bài tập" : "Món ăn";
    }
}
