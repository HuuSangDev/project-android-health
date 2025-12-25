package com.example.app_selfcare.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Data.Model.Response.NotificationResponse;
import com.example.app_selfcare.R;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationResponse> notificationList;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationResponse notification);
        void onDeleteClick(NotificationResponse notification);
    }

    public NotificationAdapter(List<NotificationResponse> notificationList, OnNotificationClickListener listener) {
        this.notificationList = notificationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationResponse notification = notificationList.get(position);

        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());
        holder.tvTime.setText(formatTime(notification.getCreatedAt()));

        // Hiển thị type và goal
        String typeText = mapType(notification.getType());
        String goalText = mapGoal(notification.getGoal());
        
        if (goalText != null && !goalText.isEmpty()) {
            holder.tvType.setText(typeText + " • " + goalText);
        } else {
            holder.tvType.setText(typeText + " • Tất cả");
        }

        // Hiển thị trạng thái đã đọc/chưa đọc
        if (notification.isRead()) {
            holder.cardNotification.setAlpha(0.7f);
            holder.ivStatus.setImageResource(R.drawable.ic_check_circle);
            holder.ivStatus.setColorFilter(holder.itemView.getContext().getColor(R.color.green));
        } else {
            holder.cardNotification.setAlpha(1.0f);
            holder.ivStatus.setImageResource(R.drawable.ic_circle);
            holder.ivStatus.setColorFilter(holder.itemView.getContext().getColor(R.color.orange));
        }

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClick(notification);
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    private String formatTime(String createdAt) {
        try {
            // Parse ISO datetime string
            LocalDateTime dateTime = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault());
            return dateTime.format(formatter);
        } catch (Exception e) {
            return createdAt; // Return original if parsing fails
        }
    }

    private String mapType(String type) {
        if (type == null) return "Thông báo";
        switch (type.toUpperCase()) {
            case "FOOD":
                return "Món ăn";
            case "EXERCISE":
                return "Bài tập";
            default:
                return type;
        }
    }

    private String mapGoal(String goal) {
        if (goal == null) return "";
        switch (goal.toUpperCase()) {
            case "WEIGHT_LOSS":
                return "Giảm cân";
            case "MUSCLE_GAIN":
                return "Tăng cơ";
            case "MAINTAIN_WEIGHT":
                return "Duy trì";
            default:
                return goal;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardNotification;
        TextView tvTitle, tvMessage, tvType, tvTime;
        ImageView ivStatus, ivDelete;

        ViewHolder(View itemView) {
            super(itemView);
            cardNotification = itemView.findViewById(R.id.cardNotification);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvType = itemView.findViewById(R.id.tvType);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivStatus = itemView.findViewById(R.id.ivStatus);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}