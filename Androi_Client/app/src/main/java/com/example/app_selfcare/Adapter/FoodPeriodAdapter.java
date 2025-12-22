// File: app/src/main/java/com/example/app_selfcare/Adapter/FoodPeriodAdapter.java
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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.app_selfcare.R;
import com.example.app_selfcare.RecipeDetailActivity;
import com.example.app_selfcare.Data.Model.Food;

import java.util.ArrayList;
import java.util.List;

public class FoodPeriodAdapter extends RecyclerView.Adapter<FoodPeriodAdapter.FoodViewHolder> {

    private final Context context;
    private List<Food> foodList = new ArrayList<>();

    public FoodPeriodAdapter(Context context) {
        this.context = context;
    }

    // Gọi hàm để cập nhật dữ liệu
    public void setFoodList(List<Food> foodList) {
        this.foodList.clear();
        if (foodList != null) {
            this.foodList.addAll(foodList);
            // Debug: In ra để kiểm tra
            for (Food f : foodList) {
                android.util.Log.d("ADAPTER_DEBUG", "Received food: " + f.getName() + " | imageUrl: " + f.getImageUrl());
            }
        }
        android.util.Log.d("ADAPTER_DEBUG", "Total items in adapter: " + this.foodList.size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_card, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);

        holder.tvRecipeName.setText(food.getName());
        holder.tvRecipeDescription.setText(food.getDescription());
        holder.tvTime.setText(food.getTimeMinutes() + " phút");
        holder.tvCalories.setText(food.getCalories() + " kcal");
        holder.tvDifficulty.setText(food.getDifficulty());

        // Load ảnh từ imageUrl bằng Glide
        String imageUrl = food.getImageUrl();
        android.util.Log.d("GLIDE_DEBUG", "Loading image for: " + food.getName() + " | URL: " + imageUrl);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_salad)
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            android.util.Log.e("GLIDE_ERROR", "FAILED to load: " + imageUrl);
                            if (e != null) {
                                e.logRootCauses("GLIDE_ERROR");
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            android.util.Log.d("GLIDE_SUCCESS", "SUCCESS loaded: " + imageUrl);
                            return false;
                        }
                    })
                    .error(R.drawable.ic_salad)
                    .into(holder.ivRecipeImage);
        } else {
            holder.ivRecipeImage.setImageResource(R.drawable.ic_salad);
        }

        // Hiển thị badge bữa ăn (S
        String mealType = food.getMealType();
        if (mealType != null) {
            switch (mealType) {
                case "BREAKFAST":
                    holder.tvMealType.setText("Sáng");
                    holder.tvMealType.setBackgroundResource(R.drawable.bg_badge_breakfast);
                    holder.tvMealType.setVisibility(View.VISIBLE);
                    break;
                case "LUNCH":
                    holder.tvMealType.setText("Trưa");
                    holder.tvMealType.setBackgroundResource(R.drawable.bg_badge_lunch);
                    holder.tvMealType.setVisibility(View.VISIBLE);
                    break;
                case "DINNER":
                    holder.tvMealType.setText("Tối");
                    holder.tvMealType.setBackgroundResource(R.drawable.bg_badge_dinner);
                    holder.tvMealType.setVisibility(View.VISIBLE);
                    break;
                default:
                    holder.tvMealType.setVisibility(View.GONE);
                    break;
            }
        } else {
            holder.tvMealType.setVisibility(View.GONE);
        }

        // Click vào card → mở chi tiết món ăn
        holder.cardRecipe.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            try {
                // Chuyển foodId từ String sang int để khớp với RecipeDetailActivity
                int id = Integer.parseInt(food.getId());
                intent.putExtra("foodId", id);
            } catch (NumberFormatException e) {
                // Nếu không phải số (trường hợp dữ liệu cũ), truyền -1
                intent.putExtra("foodId", -1);
            }
            intent.putExtra("foodName", food.getName());
            intent.putExtra("foodCalories", food.getCalories());
            intent.putExtra("foodTime", food.getTimeMinutes());
            intent.putExtra("foodDifficulty", food.getDifficulty());
            intent.putExtra("foodDescription", food.getDescription());
            intent.putExtra("foodImageUrl", food.getImageUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    // Đổi tên class ViewHolder cho đúng
    static class FoodViewHolder extends RecyclerView.ViewHolder {
        CardView cardRecipe;
        ImageView ivRecipeImage;
        TextView tvRecipeName, tvRecipeDescription;
        TextView tvTime, tvCalories, tvDifficulty, tvMealType;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRecipe = itemView.findViewById(R.id.cardRecipe);
            ivRecipeImage = itemView.findViewById(R.id.ivRecipeImage);
            tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
            tvRecipeDescription = itemView.findViewById(R.id.tvRecipeDescription);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
            tvMealType = itemView.findViewById(R.id.tvMealType);
        }
    }
}