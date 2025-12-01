// File: app/src/main/java/com/example/app_selfcare/Adapter/SavedRecipeAdapter.java
package com.example.app_selfcare.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Data.Model.Recipe;
import com.example.app_selfcare.R;

import java.util.List;

public class SavedRecipeAdapter extends RecyclerView.Adapter<SavedRecipeAdapter.ViewHolder> {

    private final List<Recipe> recipeList;  // Dùng model chung
    private final Context context;

    public SavedRecipeAdapter(List<Recipe> recipeList, Context context) {
        this.recipeList = recipeList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saved_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.tvRecipeName.setText(recipe.getName());
        holder.tvRecipeTime.setText(recipe.getTimeMinutes() + " phút");
        holder.ivRecipeImage.setImageResource(recipe.getImageResId());

        // Bonus đẹp hơn: hiển thị calo + độ khó
        // Nếu bạn có TextView khác thì thêm vào item layout nhé
    }

    @Override
    public int getItemCount() {
        return recipeList != null ? recipeList.size() : 0;
    }

    // ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRecipeImage;
        TextView tvRecipeName;
        TextView tvRecipeTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRecipeImage = itemView.findViewById(R.id.iv_recipe_image);
            tvRecipeName = itemView.findViewById(R.id.tv_recipe_name);
            tvRecipeTime = itemView.findViewById(R.id.tv_recipe_time);
        }
    }
}