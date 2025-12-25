package com.example.app_selfcare.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.app_selfcare.Data.Model.SearchHistory;
import com.example.app_selfcare.Data.Model.SearchItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchHistoryManager {
    private static final String PREFS_NAME = "SEARCH_HISTORY";
    private static final String KEY_HISTORY = "history_list";
    private static final int MAX_HISTORY_SIZE = 20;

    private final SharedPreferences prefs;
    private final Gson gson;

    public SearchHistoryManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    /**
     * Thêm item vào lịch sử tìm kiếm
     */
    public void addToHistory(SearchItem item, String imageUrl, String category) {
        List<SearchHistory> history = getHistory();
        
        // Xóa item cũ nếu đã tồn tại (để tránh duplicate)
        history.removeIf(h -> h.getId() == item.getId() && h.getType() == item.getType());
        
        // Thêm item mới vào đầu danh sách
        SearchHistory newHistory = new SearchHistory(
            item.getId(),
            item.getName(),
            item.getType(),
            imageUrl,
            category
        );
        history.add(0, newHistory);
        
        // Giới hạn số lượng lịch sử
        if (history.size() > MAX_HISTORY_SIZE) {
            history = history.subList(0, MAX_HISTORY_SIZE);
        }
        
        saveHistory(history);
    }

    /**
     * Lấy danh sách lịch sử tìm kiếm
     */
    public List<SearchHistory> getHistory() {
        String json = prefs.getString(KEY_HISTORY, "[]");
        Type listType = new TypeToken<List<SearchHistory>>(){}.getType();
        List<SearchHistory> history = gson.fromJson(json, listType);
        
        if (history == null) {
            history = new ArrayList<>();
        }
        
        // Sắp xếp theo thời gian (mới nhất trước)
        Collections.sort(history, (h1, h2) -> Long.compare(h2.getTimestamp(), h1.getTimestamp()));
        
        return history;
    }

    /**
     * Lấy lịch sử tìm kiếm dưới dạng SearchItem
     */
    public List<SearchItem> getHistoryAsSearchItems() {
        List<SearchHistory> history = getHistory();
        List<SearchItem> items = new ArrayList<>();
        
        for (SearchHistory h : history) {
            items.add(h.toSearchItem());
        }
        
        return items;
    }

    /**
     * Xóa một item khỏi lịch sử
     */
    public void removeFromHistory(int id, int type) {
        List<SearchHistory> history = getHistory();
        history.removeIf(h -> h.getId() == id && h.getType() == type);
        saveHistory(history);
    }

    /**
     * Xóa toàn bộ lịch sử
     */
    public void clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply();
    }

    /**
     * Lưu lịch sử vào SharedPreferences
     */
    private void saveHistory(List<SearchHistory> history) {
        String json = gson.toJson(history);
        prefs.edit().putString(KEY_HISTORY, json).apply();
    }

    /**
     * Kiểm tra xem có lịch sử không
     */
    public boolean hasHistory() {
        return !getHistory().isEmpty();
    }

    /**
     * Lấy số lượng item trong lịch sử
     */
    public int getHistoryCount() {
        return getHistory().size();
    }
}