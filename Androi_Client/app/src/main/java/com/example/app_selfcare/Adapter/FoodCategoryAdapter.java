package com.example.app_selfcare.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Data.Model.Response.FoodCategoryResponse;
import com.example.app_selfcare.R;

import java.util.ArrayList;
import java.util.List;

public class FoodCategoryAdapter extends RecyclerView.Adapter<FoodCategoryAdapter.ViewHolder> {

    private List<FoodCategoryResponse> categoryList = new ArrayList<>();
    private OnCategoryActionListener listener;

    public interface OnCategoryActionListener {
        void onEditClick(FoodCategoryResponse category);
        void onDeleteClick(FoodCategoryResponse category);
    }

    public FoodCategoryAdapter(OnCategoryActionListener listener) {
        this.listener = listener;
    }

    public void setData(List<FoodCategoryResponse> categories) {
        this.categoryList.clear();
        if (categories != null) {
            this.categoryList.addAll(categories);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodCategoryResponse category = categoryList.get(position);

        holder.tvCategoryName.setText(category.getCategoryName());
        
        if (category.getDescription() != null && !category.getDescription().isEmpty()) {
            holder.tvDescription.setText(category.getDescription());
            holder.tvDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(category);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(category);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvDescription;
        ImageButton btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
