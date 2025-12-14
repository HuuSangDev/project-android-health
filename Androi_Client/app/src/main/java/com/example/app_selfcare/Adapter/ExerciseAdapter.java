package com.example.app_selfcare.Adapter;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseVH> {

    private final Context context;
    private final List<ExerciseResponse> data = new ArrayList<>();

    public ExerciseAdapter(Context context) {
        this.context = context;
    }

    public void submit(List<ExerciseResponse> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExerciseVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseVH holder, int position) {
        ExerciseResponse item = data.get(position);
        holder.tvName.setText(item.getExerciseName());
        holder.tvDesc.setText(item.getDescription() != null ? item.getDescription() : "");
        holder.tvCalories.setText(String.format("%.0f kcal/ph", item.getCaloriesPerMinute() != null ? item.getCaloriesPerMinute() : 0));
        holder.tvDifficulty.setText(item.getDifficultyLevel() != null ? item.getDifficultyLevel() : "LEVEL");

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.img_dumbbell)
                .error(R.drawable.img_dumbbell)
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ExerciseVH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvDesc, tvCalories, tvDifficulty;

        public ExerciseVH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgExercise);
            tvName = itemView.findViewById(R.id.tvExerciseName);
            tvDesc = itemView.findViewById(R.id.tvExerciseDesc);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
        }
    }
}

