package com.example.app_selfcare.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_selfcare.Data.Model.Response.ExerciseResponse;
import com.example.app_selfcare.R;
import com.example.app_selfcare.WorkoutActivity;

import java.util.List;

public class WorkoutSliderAdapter extends RecyclerView.Adapter<WorkoutSliderAdapter.WorkoutViewHolder> {

    private Context context;
    private List<ExerciseResponse> exercises;

    public WorkoutSliderAdapter(Context context, List<ExerciseResponse> exercises) {
        this.context = context;
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_workout_slider, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        ExerciseResponse exercise = exercises.get(position);
        
        holder.tvWorkoutName.setText(exercise.getExerciseName());
        holder.tvCalories.setText((double) exercise.getCaloriesPerMinute() + " cal/phút");
        holder.tvDifficulty.setText(mapDifficulty(exercise.getDifficultyLevel()));
        
        // Load image
        if (exercise.getImageUrl() != null && !exercise.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(exercise.getImageUrl())
                    .placeholder(R.drawable.ic_workout)
                    .error(R.drawable.ic_workout)
                    .centerCrop()
                    .into(holder.ivWorkout);
        } else {
            holder.ivWorkout.setImageResource(R.drawable.ic_workout);
        }
        
        // Click listener
        holder.cardWorkout.setOnClickListener(v -> {
            Intent intent = new Intent(context, WorkoutActivity.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return exercises != null ? exercises.size() : 0;
    }

    private String mapDifficulty(String level) {
        if (level == null) return "Dễ";
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

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        CardView cardWorkout;
        ImageView ivWorkout;
        TextView tvWorkoutName, tvCalories, tvDifficulty;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            cardWorkout = itemView.findViewById(R.id.cardWorkout);
            ivWorkout = itemView.findViewById(R.id.ivWorkout);
            tvWorkoutName = itemView.findViewById(R.id.tvWorkoutName);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
        }
    }
}