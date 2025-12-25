// app/src/main/java/com/example/app_selfcare/Adapter/ExerciseAdapter.java
package com.example.app_selfcare.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.app_selfcare.Data.Model.Exercise;
import com.example.app_selfcare.R;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private final List<Exercise> exerciseList;
    private final OnExerciseClickListener listener;
    private OnItemClickListener itemClickListener;

    // Interface cho Admin (edit/delete)
    public interface OnExerciseClickListener {
        void onEdit(Exercise exercise);
        void onDelete(int id);
    }

    // Interface đơn giản cho User (click xem chi tiết)
    public interface OnItemClickListener {
        void onItemClick(Exercise exercise);
    }

    public ExerciseAdapter(List<Exercise> exerciseList, OnExerciseClickListener listener) {
        this.exerciseList = exerciseList;
        this.listener = listener;
        this.itemClickListener = null;
    }

    // Constructor đơn giản dùng cho WorkoutActivity
    public ExerciseAdapter(List<Exercise> exerciseList) {
        this.exerciseList = exerciseList;
        this.listener = null;
        this.itemClickListener = null;
    }

    // Setter cho item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);

        holder.tvName.setText(exercise.getName());

        // Display calories per minute and difficulty
        String info = String.format("%.1f kcal/min", exercise.getCaloriesPerMinute());
        if (exercise.getDifficultyLevel() != null && !exercise.getDifficultyLevel().isEmpty()) {
            info += " • " + exercise.getDifficultyLevel();
        }
        holder.tvInfo.setText(info);

        holder.tvCategory.setText(exercise.getCategoryName() != null ? exercise.getCategoryName() : "");

        // Load image using Glide from imageUrl
        if (exercise.getImageUrl() != null && !exercise.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(exercise.getImageUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_workout)
                            .error(R.drawable.ic_workout))
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.ic_workout);
        }

        // Click listener for Admin (edit/delete)
        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onEdit(exercise));
            if (holder.btnDelete != null) {
                holder.btnDelete.setOnClickListener(v -> listener.onDelete(exercise.getId()));
            }
        } 
        // Click listener for User (view details)
        else if (itemClickListener != null) {
            holder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(exercise));
        }
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvInfo, tvCategory;
        ImageView ivImage, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_exercise_name);
            tvInfo = itemView.findViewById(R.id.tv_exercise_info);
            tvCategory = itemView.findViewById(R.id.tv_exercise_category);
            ivImage = itemView.findViewById(R.id.iv_exercise_image);
//            btnDelete = itemView.findViewById(R.id.btn_delete_exercise);
        }
    }
}