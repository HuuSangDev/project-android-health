// app/src/main/java/com/example/app_selfcare/Adapter/ExerciseAdapter.java
package com.example.app_selfcare.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Data.Model.Exercise;
import com.example.app_selfcare.R;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private final List<Exercise> exerciseList;
    private final OnExerciseClickListener listener;

    public interface OnExerciseClickListener {
        void onEdit(Exercise exercise);
        void onDelete(int id);
    }

    public ExerciseAdapter(List<Exercise> exerciseList, OnExerciseClickListener listener) {
        this.exerciseList = exerciseList;
        this.listener = listener;
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

        holder.tvName.setText(exercise.name);
        holder.tvInfo.setText(exercise.durationMinutes + " phút • " + exercise.caloriesBurned + " kcal • " + exercise.difficulty);
        holder.tvCategory.setText(exercise.categoryId); // hoặc tên category nếu có
        holder.ivImage.setImageResource(exercise.imageResId);

        holder.itemView.setOnClickListener(v -> listener.onEdit(exercise));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(exercise.id));
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
            btnDelete = itemView.findViewById(R.id.btn_delete_exercise);
        }
    }
}