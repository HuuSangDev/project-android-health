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
import com.example.app_selfcare.Data.Model.Response.FoodResponse;
import com.example.app_selfcare.R;
import com.example.app_selfcare.RecipeHomeActivity;

import java.util.List;

public class FoodSliderAdapter extends RecyclerView.Adapter<FoodSliderAdapter.FoodViewHolder> {

    private Context context;
    private List<FoodResponse> foods;

    public FoodSliderAdapter(Context context, List<FoodResponse> foods) {
        this.context = context;
        this.foods = foods;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food_slider, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodResponse food = foods.get(position);
        
        holder.tvFoodName.setText(food.getFoodName());
        
        // Hiển thị calories
        holder.tvCalories.setText((int) food.getCaloriesPer100g() + " cal");
        
        // Hiển thị thời gian nấu (prep + cook time)
        int totalTime = food.getPrepTime() + food.getCookTime();
        holder.tvCookTime.setText(totalTime + " phút");
        
        // Load image
        if (food.getImageUrl() != null && !food.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(food.getImageUrl())
                    .placeholder(R.drawable.ic_salad)
                    .error(R.drawable.ic_salad)
                    .centerCrop()
                    .into(holder.ivFood);
        } else {
            holder.ivFood.setImageResource(R.drawable.ic_salad);
        }
        
        // Click listener
        holder.cardFood.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeHomeActivity.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foods != null ? foods.size() : 0;
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        CardView cardFood;
        ImageView ivFood;
        TextView tvFoodName, tvCalories, tvCookTime;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            cardFood = itemView.findViewById(R.id.cardFood);
            ivFood = itemView.findViewById(R.id.ivFood);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvCalories = itemView.findViewById(R.id.tvCalories);
            tvCookTime = itemView.findViewById(R.id.tvCookTime);
        }
    }
}