package com.example.app_selfcare.Data.Model;

public class SearchHistory {
    private int id;
    private String name;
    private int type; // SearchItem.TYPE_FOOD or TYPE_WORKOUT
    private String imageUrl;
    private long timestamp;
    private String category;

    public SearchHistory() {}

    public SearchHistory(int id, String name, int type, String imageUrl, String category) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.imageUrl = imageUrl;
        this.category = category;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTypeLabel() {
        return type == SearchItem.TYPE_WORKOUT ? "Bài tập" : "Món ăn";
    }

    // Convert to SearchItem
    public SearchItem toSearchItem() {
        return new SearchItem(id, name, type, imageUrl);
    }
}