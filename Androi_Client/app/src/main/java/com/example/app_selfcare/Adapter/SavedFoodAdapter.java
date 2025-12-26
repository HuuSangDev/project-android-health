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
import com.example.app_selfcare.Data.Model.Response.SavedFoodResponse;
import com.example.app_selfcare.RecipeDetailActivity;
import com.example.app_selfcare.R;

import java.util.List;

/**
 * SavedFoodAdapter - Adapter hiển thị danh sách món ăn đã lưu của user
 * 
 * Chức năng:
 * - Hiển thị thông tin món ăn đã lưu (tên, ảnh, calories, thời gian)
 * - Click vào món ăn để xem chi tiết (RecipeDetailActivity)
 */
public class SavedFoodAdapter extends RecyclerView.Adapter<SavedFoodAdapter.SavedFoodViewHolder> {

    private List<SavedFoodResponse> savedFoodList;
    private Context context;

    public SavedFoodAdapter(List<SavedFoodResponse> savedFoodList, Context context) {
        this.savedFoodList = savedFoodList;
        this.context = context;
    }

    @NonNull
    @Override
    public SavedFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_food, parent, false);
        return new SavedFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedFoodViewHolder holder, int position) {
        SavedFoodResponse savedFood = savedFoodList.get(position);
        
        holder.tvFoodName.setText(savedFood.getFoodName());
        holder.tvCalories.setText(savedFood.getFormattedCalories());
        holder.tvTime.setText(savedFood.getTotalTime() + " phút");
        
        // Load image
        Glide.with(context)
                .load(savedFood.getImageUrl())
                .placeholder(R.drawable.ic_platter_background)
                .error(R.drawable.ic_platter_background)
                .into(holder.ivFoodImage);

        // Click để xem chi tiết món ăn (màn hình user)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("foodId", savedFood.getFoodId().intValue());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return savedFoodList.size();
    }

    public void updateData(List<SavedFoodResponse> newSavedFoodList) {
        this.savedFoodList.clear();
        this.savedFoodList.addAll(newSavedFoodList);
        notifyDataSetChanged();
    }

    static class SavedFoodViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoodImage;
        TextView tvFoodName, tvCalories, tvTime;

        public SavedFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.iv_food_image);
            tvFoodName = itemView.findViewById(R.id.tv_food_name);
            tvCalories = itemView.findViewById(R.id.tv_calories);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}