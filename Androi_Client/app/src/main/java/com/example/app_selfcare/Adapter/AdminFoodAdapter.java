package com.example.app_selfcare.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_selfcare.Data.Model.Response.FoodResponse;
import com.example.app_selfcare.R;

import java.util.ArrayList;
import java.util.List;

public class AdminFoodAdapter extends RecyclerView.Adapter<AdminFoodAdapter.ViewHolder> {

    private List<FoodResponse> foodList = new ArrayList<>();
    private OnFoodClickListener listener;

    public interface OnFoodClickListener {
        void onFoodClick(FoodResponse food);
    }

    public AdminFoodAdapter(OnFoodClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<FoodResponse> foods) {
        this.foodList.clear();
        if (foods != null) {
            this.foodList.addAll(foods);
        }
        notifyDataSetChanged();
    }

    public void addData(List<FoodResponse> foods) {
        if (foods != null) {
            int startPos = foodList.size();
            foodList.addAll(foods);
            notifyItemRangeInserted(startPos, foods.size());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodResponse food = foodList.get(position);

        holder.tvFoodName.setText(food.getFoodName());
        holder.tvCalories.setText(String.format("%.0f kcal", food.getCaloriesPer100g()));
        
        // Category
        if (food.getCategoryResponse() != null) {
            holder.tvCategory.setText(food.getCategoryResponse().getCategoryName());
            holder.tvCategory.setVisibility(View.VISIBLE);
        } else {
            holder.tvCategory.setVisibility(View.GONE);
        }

        // MealType
        holder.tvMealType.setText(mapMealType(food.getMealType()));

        // Image
        if (food.getImageUrl() != null && !food.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(food.getImageUrl())
                    .placeholder(R.drawable.ic_food_placeholder)
                    .error(R.drawable.ic_food_placeholder)
                    .centerCrop()
                    .into(holder.ivFood);
        } else {
            holder.ivFood.setImageResource(R.drawable.ic_food_placeholder);
        }

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onFoodClick(food);
        });
    }

    private String mapMealType(String mealType) {
        if (mealType == null) return "";
        switch (mealType.toUpperCase()) {
            case "BREAKFAST": return "Bữa sáng";
            case "LUNCH": return "Bữa trưa";
            case "DINNER": return "Bữa tối";
            case "ALL": return "Tất cả";
            default: return mealType;
        }
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFood;
        TextView tvFoodName, tvCalories, tvCategory, tvMealType;

        ViewHolder(View itemView) {
            super(itemView);
            ivFood = itemView.findViewById(R.id.ivFood);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvMealType = itemView.findViewById(R.id.tvMealType);
        }
    }
}
