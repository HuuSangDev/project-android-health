package com.example.app_selfcare.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.app_selfcare.Data.Model.SearchItem;
import com.example.app_selfcare.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final List<SearchItem> list;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SearchItem item);
    }

    public SearchAdapter(List<SearchItem> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchItem item = list.get(position);

        holder.tvTitle.setText(item.getName());
        holder.tvType.setText(item.getTypeLabel());

        // Set icon và màu dựa trên type
        if (item.getType() == SearchItem.TYPE_FOOD) {
            // Nếu có imageUrl thì load ảnh, không thì dùng icon mặc định
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.ic_food)
                    .error(R.drawable.ic_food)
                    .transform(new CircleCrop())
                    .into(holder.imgIcon);
            } else {
                holder.imgIcon.setImageResource(R.drawable.ic_food);
                holder.imgIcon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.orange_primary, null));
            }
        } else if (item.getType() == SearchItem.TYPE_WORKOUT) {
            // Nếu có imageUrl thì load ảnh, không thì dùng icon mặc định
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.ic_exercise)
                    .error(R.drawable.ic_exercise)
                    .transform(new CircleCrop())
                    .into(holder.imgIcon);
            } else {
                holder.imgIcon.setImageResource(R.drawable.ic_exercise);
                holder.imgIcon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.blue_primary, null));
            }
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvType;
        ImageView imgIcon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvType = itemView.findViewById(R.id.tvType);
            imgIcon = itemView.findViewById(R.id.imgIcon);
        }
    }
}
