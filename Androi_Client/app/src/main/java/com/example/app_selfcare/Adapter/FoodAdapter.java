// File: app/src/main/java/com/example/app_selfcare/Adapter/FoodAdapter.java
package com.example.app_selfcare.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_selfcare.Data.Model.Food;
import com.example.app_selfcare.R;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private final List<Food> foodList;

    public FoodAdapter(List<Food> foodList) {
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.tvFoodName.setText(food.getName());
        holder.tvInfo.setText(food.getCalories() + " kcal • " + food.getTimeMinutes() + " phút • " + food.getDifficulty());
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvInfo;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvInfo = itemView.findViewById(R.id.tvFoodInfo);
        }
    }
}