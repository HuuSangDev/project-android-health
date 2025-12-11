package com.example.app_selfcare.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Data.Model.User;
import com.example.app_selfcare.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final List<User> userList;
    private final OnUserClickListener listener;
    private final Context context; // Thêm context để dùng Toast an toàn

    // Interface để bắt sự kiện từ Activity/Fragment
    public interface OnUserClickListener {
        void onEdit(User user);
        void onBlock(String uid);
    }

    // Constructor bắt buộc truyền Context
    public UserAdapter(Context context, List<User> userList, OnUserClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvName.setText(user.name != null ? user.name : "Chưa có tên");
        holder.tvEmail.setText(user.email != null ? user.email : "Chưa có email");
        holder.tvStats.setText(
                user.heightCm + "cm • " +
                        user.weightKg + "kg • Mục tiêu: " +
                        user.goalWeightKg + "kg"
        );

        // TODO: Nếu có ảnh avatar từ Firebase Storage hoặc Glide, thêm ở đây
        // Glide.with(context).load(user.avatarUrl).placeholder(R.drawable.ic_user_default).into(holder.ivAvatar);

        // Click vào cả item → sửa người dùng
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(user);
            }
        });

        // Click nút chặn → hiện Toast + gọi callback
        holder.btnBlock.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBlock(user.uid);

                // HIỆN TOAST THÀNH CÔNG Ở ĐÂY
                Toast.makeText(context, "Đã chặn người dùng " + user.name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    // ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvStats;
        ImageView ivAvatar, btnBlock;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_user_name);
            tvEmail = itemView.findViewById(R.id.tv_user_email);
            tvStats = itemView.findViewById(R.id.tv_user_stats);
            ivAvatar = itemView.findViewById(R.id.iv_user_avatar);
            btnBlock = itemView.findViewById(R.id.btn_block_user);
        }
    }

    // Hàm tiện ích (tuỳ chọn) để Toast nhanh ở nơi khác trong Adapter
    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}