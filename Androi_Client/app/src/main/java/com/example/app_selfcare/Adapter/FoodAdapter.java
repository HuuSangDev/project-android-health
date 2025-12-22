// File: app/src/main/java/com/example/app_selfcare/Adapter/FoodAdapter.java
package com.example.app_selfcare.Adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable; // Import thêm cái này
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener; // Import Listener
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.app_selfcare.Data.Model.Food;
import com.example.app_selfcare.R;
import com.example.app_selfcare.RecipeDetailActivity;

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

        // Kiểm tra null cho chắc chắn
        String calories = String.valueOf(food.getCalories());
        String time = String.valueOf(food.getTimeMinutes());
        String diff = food.getDifficulty() != null ? food.getDifficulty() : "";

        holder.tvFoodInfo.setText(calories + " kcal • " + time + " phút • " + diff);

        // --- BẮT ĐẦU ĐOẠN LOAD ẢNH CÓ LISTENER ---
        String imageUrl = food.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .override(300, 300) // Thu nhỏ ảnh để tránh lỗi GL/Lag
                            .centerCrop()
                            .transform(new RoundedCorners(12))
                    )
                    .placeholder(R.drawable.ic_platter_background)
                    // GẮN MÁY NGHE LÉN VÀO ĐÂY
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // Khi ảnh lỗi, nó sẽ chạy vào đây và in lỗi ra Logcat
                            Log.e("GLIDE_ERROR", "Lỗi tải ảnh món: " + food.getName());
                            Log.e("GLIDE_ERROR", "Link ảnh: " + imageUrl);
                            if (e != null) {
                                e.logRootCauses("GLIDE_ERROR"); // In nguyên nhân gốc rễ
                            }
                            return false; // Để Glide tự xử lý hiển thị ảnh lỗi (placeholder)
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            // Ảnh tải thành công
                            return false;
                        }
                    })
                    .error(R.drawable.ic_platter_background)
                    .into(holder.ivFoodImage);
        } else {
            holder.ivFoodImage.setImageResource(R.drawable.ic_platter_background);
        }
        // ---------------------------------------------

        // Click vào món ăn mở chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RecipeDetailActivity.class);
            try {
                // Parse ID an toàn
                if (food.getId() != null) {
                    // Đề phòng ID dạng "3.0"
                    double d = Double.parseDouble(food.getId());
                    intent.putExtra("foodId", (int) d);
                } else {
                    intent.putExtra("foodId", -1);
                }
            } catch (NumberFormatException e) {
                intent.putExtra("foodId", -1);
            }
            intent.putExtra("foodName", food.getName());
            // Nên truyền thêm ảnh sang detail để đỡ phải load lại
            intent.putExtra("foodImageUrl", food.getImageUrl());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvFoodInfo;
        ImageView ivFoodImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodInfo = itemView.findViewById(R.id.tvFoodInfo);
            ivFoodImage = itemView.findViewById(R.id.ivFoodImage);
        }
    }
}