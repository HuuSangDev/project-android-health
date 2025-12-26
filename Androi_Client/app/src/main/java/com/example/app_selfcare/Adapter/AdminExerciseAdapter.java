package com.example.app_selfcare.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_selfcare.Data.Model.Response.ExerciseResponse;
import com.example.app_selfcare.R;

import java.util.List;

/**
 * AdminExerciseAdapter - Adapter hiển thị danh sách bài tập cho Admin
 * 
 * Chức năng:
 * - Hiển thị thông tin bài tập
 * - Nút sửa và xóa bài tập
 */
public class AdminExerciseAdapter extends RecyclerView.Adapter<AdminExerciseAdapter.ViewHolder> {

    private final List<ExerciseResponse> exerciseList;
    private final OnExerciseActionListener listener;

    public interface OnExerciseActionListener {
        void onEdit(ExerciseResponse exercise);
        void onDelete(ExerciseResponse exercise);
    }

    public AdminExerciseAdapter(List<ExerciseResponse> exerciseList, OnExerciseActionListener listener) {
        this.exerciseList = exerciseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseResponse exercise = exerciseList.get(position);

        holder.tvName.setText(exercise.getExerciseName());
        
        // Hiển thị calories và độ khó
        String info = String.format("%.1f kcal/phút", exercise.getCaloriesPerMinute());
        if (exercise.getDifficultyLevel() != null) {
            info += " • " + mapDifficulty(exercise.getDifficultyLevel());
        }
        holder.tvInfo.setText(info);

        // Hiển thị danh mục
        if (exercise.getCategory() != null) {
            holder.tvCategory.setText(exercise.getCategory().getCategoryName());
            holder.tvCategory.setVisibility(View.VISIBLE);
        } else {
            holder.tvCategory.setVisibility(View.GONE);
        }

        // Hiển thị mục tiêu
        if (exercise.getGoal() != null) {
            holder.tvGoal.setText(mapGoal(exercise.getGoal()));
            holder.tvGoal.setVisibility(View.VISIBLE);
        } else {
            holder.tvGoal.setVisibility(View.GONE);
        }

        // Load image
        if (exercise.getImageUrl() != null && !exercise.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(exercise.getImageUrl())
                    .placeholder(R.drawable.ic_workout)
                    .error(R.drawable.ic_workout)
                    .centerCrop()
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.ic_workout);
        }

        // Click listeners
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(exercise);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(exercise);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(exercise);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    /**
     * Map difficulty level từ tiếng Anh sang tiếng Việt
     */
    private String mapDifficulty(String level) {
        if (level == null) return "";
        switch (level.toUpperCase()) {
            case "BEGINNER":
                return "Dễ";
            case "INTERMEDIATE":
                return "Trung bình";
            case "ADVANCED":
                return "Khó";
            default:
                return level;
        }
    }

    /**
     * Map goal từ enum sang tiếng Việt
     */
    private String mapGoal(String goal) {
        if (goal == null) return "";
        switch (goal.toUpperCase()) {
            case "WEIGHT_LOSS":
                return "Giảm cân";
            case "MAINTAIN":
                return "Duy trì";
            case "WEIGHT_GAIN":
                return "Tăng cân";
            default:
                return goal;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvInfo, tvCategory, tvGoal;
        ImageView btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_exercise_image);
            tvName = itemView.findViewById(R.id.tv_exercise_name);
            tvInfo = itemView.findViewById(R.id.tv_exercise_info);
            tvCategory = itemView.findViewById(R.id.tv_exercise_category);
            tvGoal = itemView.findViewById(R.id.tv_exercise_goal);
            btnEdit = itemView.findViewById(R.id.btn_edit_exercise);
            btnDelete = itemView.findViewById(R.id.btn_delete_exercise);
        }
    }
}
