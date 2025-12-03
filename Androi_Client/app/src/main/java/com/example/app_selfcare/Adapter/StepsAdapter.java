package com.example.app_selfcare.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Data.Model.Step;
import com.example.app_selfcare.R;

import java.util.List;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {

    private final List<Step> steps;

    public StepsAdapter(List<Step> steps) {
        this.steps = steps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Dùng layout có sẵn của Android → KHÔNG CẦN TẠO FILE XML
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Step step = steps.get(position);

        // Dòng 1: Số thứ tự + tiêu đề ngắn (nếu có)
        holder.tvTitle.setText(step.getNumber() + ". Bước " + step.getNumber());

        // Dòng 2: Mô tả chi tiết
        holder.tvDescription.setText(step.getDescription());
    }

    @Override
    public int getItemCount() {
        return steps != null ? steps.size() : 0;
    }

    // ViewHolder dùng 2 TextView có sẵn
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;       // android.R.id.text1
        TextView tvDescription; // android.R.id.text2

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(android.R.id.text1);
            tvDescription = itemView.findViewById(android.R.id.text2);
        }
    }
}