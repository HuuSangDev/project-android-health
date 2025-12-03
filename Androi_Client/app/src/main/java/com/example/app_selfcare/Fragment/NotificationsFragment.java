package com.example.app_selfcare.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_selfcare.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private List<String> notificationList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewNotifications);
        fabAdd = view.findViewById(R.id.fabAddNotification);

        // Sample data
        notificationList = new ArrayList<>();
        notificationList.add("Cập nhật bài tập mới - Đã gửi");
        notificationList.add("Khuyến mãi đặc biệt - Đã gửi");
        notificationList.add("Thông báo bảo trì - Nháp");
        notificationList.add("Món ăn mới - Đã gửi");

        setupRecyclerView();
        setupFabButton();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO: Set adapter
    }

    private void setupFabButton() {
        fabAdd.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tạo thông báo mới", Toast.LENGTH_SHORT).show();
        });
    }
}