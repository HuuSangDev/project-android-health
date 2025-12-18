// File: app/src/main/java/com/example/app_selfcare/Adapter/SavedFoodAdapter.java
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

import com.example.app_selfcare.Data.Model.Food;
import com.example.app_selfcare.RecipeDetailActivity; // hoặc RecipeDetailActivity nếu bạn vẫn dùng tên cũ
import com.example.app_selfcare.R;

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
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foodList.get(position);

        holder.tvFoodName.setText(food.getName());
        holder.tvFoodTime.setText(food.getTimeMinutes() + " phút");
        holder.tvCalories.setText(food.getCalories() + " kcal");
        holder.tvDifficulty.setText(food.getDifficulty());

        // Nếu có ảnh thật thì dùng Glide, tạm dùng placeholder
        holder.ivFoodImage.setImageResource(R.drawable.ic_platter_background);

        // Click vào món → mở chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            try {
                int id = Integer.parseInt(food.getId());
                intent.putExtra("foodId", id);
            } catch (NumberFormatException e) {
                intent.putExtra("foodId", -1);
            }
            intent.putExtra("foodName", food.getName());
            intent.putExtra("foodCalories", food.getCalories());
            intent.putExtra("foodTime", food.getTimeMinutes());
            intent.putExtra("foodDifficulty", food.getDifficulty());
            intent.putExtra("foodDescription", food.getDescription());
            // Nếu cần truyền nguyên liệu & bước nấu → dùng Parcelable hoặc Firebase ID
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoodImage;
        TextView tvFoodName, tvFoodTime, tvCalories, tvDifficulty;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.iv_recipe_image);
            tvFoodName = itemView.findViewById(R.id.tv_recipe_name);
            tvFoodTime = itemView.findViewById(R.id.tv_recipe_time);
//            tvCalories = itemView.findViewById(R.id.tv_calories);
//            tvDifficulty = itemView.findViewById(R.id.tv_difficulty);
        }
    }
}