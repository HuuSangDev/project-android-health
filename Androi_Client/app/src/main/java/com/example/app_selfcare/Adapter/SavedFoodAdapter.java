package com.example.app_selfcare.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_selfcare.Data.Model.Food;
import com.example.app_selfcare.R;
import com.example.app_selfcare.RecipeDetailActivity;

import java.util.List;

public class SavedFoodAdapter extends RecyclerView.Adapter<SavedFoodAdapter.ViewHolder> {

    private final List<Food> foodList;
    private final Context context;

    public SavedFoodAdapter(List<Food> foodList, Context context) {
        this.foodList = foodList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_saved_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foodList.get(position);

        holder.tvFoodName.setText(food.getName());
        holder.tvFoodTime.setText("⏰ " + food.getTimeMinutes() + " phút");

        Glide.with(context)
                .load(food.getImageUrl())
                .placeholder(R.drawable.ic_platter_background)
                .error(R.drawable.ic_platter_background)
                .into(holder.ivFoodImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("foodId", food.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoodImage;
        TextView tvFoodName, tvFoodTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.iv_recipe_image);
            tvFoodName = itemView.findViewById(R.id.tv_recipe_name);
            tvFoodTime = itemView.findViewById(R.id.tv_recipe_time);
        }
    }
}
