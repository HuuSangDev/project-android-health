package com.example.app_selfcare.utils;

import android.content.Context;
import android.graphics.Color;

import com.example.app_selfcare.Data.Model.Response.DailyLogResponse;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChartManager {

    private final BarChart barChart;
    private final Context context;

    public ChartManager(BarChart barChart, Context context) {
        this.barChart = barChart;
        this.context = context;
        setupChart();
    }

    private void setupChart() {
        // Cấu hình chung
        barChart.setDrawGridBackground(false);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setPinchZoom(true);
        barChart.setBackgroundColor(Color.WHITE);

        // Legend
        barChart.getLegend().setEnabled(true);
        barChart.getLegend().setTextSize(12f);
        barChart.getLegend().setTextColor(Color.GRAY);

        // Description
        barChart.getDescription().setEnabled(false);

        // X Axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7, false); // Tối đa 7 cột

        // Y Axis - Set range từ 30 đến 150 kg
        barChart.getAxisLeft().setAxisMinimum(30f);
        barChart.getAxisLeft().setAxisMaximum(150f);
        barChart.getAxisLeft().setTextSize(10f);
        barChart.getAxisLeft().setTextColor(Color.GRAY);
        barChart.getAxisLeft().setDrawGridLines(true);
        barChart.getAxisLeft().setGridColor(Color.parseColor("#E0E0E0"));
        barChart.getAxisLeft().setGranularity(10f); // Chia thành các khoảng 10 kg

        barChart.getAxisRight().setEnabled(false);
    }

    public void updateChart(List<DailyLogResponse> logs) {
        if (logs == null || logs.isEmpty()) {
            barChart.clear();
            return;
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> xLabels = new ArrayList<>();

        // Giới hạn tối đa 7 entries
        int maxEntries = Math.min(logs.size(), 7);
        int startIndex = Math.max(0, logs.size() - 7);

        for (int i = 0; i < maxEntries; i++) {
            DailyLogResponse log = logs.get(startIndex + i);
            if (log.getCurrentWeight() != null) {
                entries.add(new BarEntry(i, log.getCurrentWeight().floatValue()));
                
                // Format date label
                String dateLabel = formatDateLabel(log.getLogDate());
                xLabels.add(dateLabel);
            }
        }

        if (entries.isEmpty()) {
            barChart.clear();
            return;
        }

        // Tạo BarDataSet với gradient color
        BarDataSet dataSet = new BarDataSet(entries, "Cân nặng (kg)");
        dataSet.setColor(Color.parseColor("#FF8C42")); // Màu cam chính
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.parseColor("#FF8C42"));
        dataSet.setBarBorderWidth(1f);
        dataSet.setBarBorderColor(Color.parseColor("#FF6B1A")); // Màu cam đậm hơn cho border
        dataSet.setHighLightColor(Color.parseColor("#FFB366")); // Màu sáng hơn khi highlight

        // Tạo BarData
        BarData barData = new BarData(dataSet);
        barData.setValueTextSize(10f);
        barData.setBarWidth(0.6f); // Giảm width để cột không quá rộng

        // Set data
        barChart.setData(barData);

        // Set X axis labels
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabels));
        barChart.getXAxis().setLabelCount(maxEntries, false);

        // Animate
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private String formatDateLabel(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
            return outputFormat.format(inputFormat.parse(dateStr));
        } catch (Exception e) {
            return dateStr;
        }
    }

    public double calculateWeightChange(List<DailyLogResponse> logs) {
        if (logs == null || logs.size() < 2) return 0;

        Double firstWeight = null;
        Double lastWeight = null;

        for (DailyLogResponse log : logs) {
            if (log.getCurrentWeight() != null) {
                if (firstWeight == null) {
                    firstWeight = log.getCurrentWeight();
                }
                lastWeight = log.getCurrentWeight();
            }
        }

        if (firstWeight != null && lastWeight != null) {
            return lastWeight - firstWeight;
        }
        return 0;
    }

    public double calculateAverageWeight(List<DailyLogResponse> logs) {
        if (logs == null || logs.isEmpty()) return 0;

        double sum = 0;
        int count = 0;

        for (DailyLogResponse log : logs) {
            if (log.getCurrentWeight() != null) {
                sum += log.getCurrentWeight();
                count++;
            }
        }

        return count > 0 ? sum / count : 0;
    }

    public Double getCurrentWeight(List<DailyLogResponse> logs) {
        if (logs == null || logs.isEmpty()) return null;

        for (int i = logs.size() - 1; i >= 0; i--) {
            if (logs.get(i).getCurrentWeight() != null) {
                return logs.get(i).getCurrentWeight();
            }
        }
        return null;
    }
}