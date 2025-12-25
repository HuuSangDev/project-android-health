package com.example.app_selfcare.Data.Model;

public class SearchItem {

    public static final int TYPE_WORKOUT = 1;
    public static final int TYPE_FOOD = 2;

    private final int id;
    private final String name;
    private final int type;
    private final String imageUrl;

    public SearchItem(int id, String name, int type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.imageUrl = null;
    }

    public SearchItem(int id, String name, int type, String imageUrl) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTypeLabel() {
        return type == TYPE_WORKOUT ? "Bài tập" : "Món ăn";
    }
}
