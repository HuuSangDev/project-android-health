package com.example.app_selfcare.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Adapter.NotificationAdapter;
import com.example.app_selfcare.Data.Model.Request.SendNotificationRequest;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.NotificationResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {

    private static final String TAG = "NotificationsFragment";

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private View layoutLoading, layoutEmpty;

    private NotificationAdapter adapter;
    private List<NotificationResponse> notificationList = new ArrayList<>();
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupApiService();
        setupRecyclerView();
        setupFabButton();
        loadNotifications();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewNotifications);
        fabAdd = view.findViewById(R.id.fabAddNotification);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
    }

    private void setupApiService() {
        apiService = ApiClient.getClientWithToken(requireContext()).create(ApiService.class);
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(notificationList, new NotificationAdapter.OnNotificationClickListener() {
            @Override
            public void onNotificationClick(NotificationResponse notification) {
                // Đánh dấu đã đọc nếu chưa đọc
                if (!notification.isRead()) {
                    markAsRead(notification.getId());
                }
            }

            @Override
            public void onDeleteClick(NotificationResponse notification) {
                // TODO: Implement delete notification if needed
                Toast.makeText(getContext(), "Xóa thông báo: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupFabButton() {
        fabAdd.setOnClickListener(v -> showCreateNotificationDialog());
    }

    private void loadNotifications() {
        showLoading(true);

        apiService.getAllNotifications().enqueue(new Callback<ApiResponse<List<NotificationResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<NotificationResponse>>> call, Response<ApiResponse<List<NotificationResponse>>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().getResult() != null) {
                    notificationList.clear();
                    notificationList.addAll(response.body().getResult());
                    adapter.notifyDataSetChanged();

                    showEmpty(notificationList.isEmpty());
                    Log.d(TAG, "Loaded " + notificationList.size() + " notifications");
                } else {
                    Log.e(TAG, "Failed to load notifications: " + response.code());
                    showError("Không thể tải danh sách thông báo");
                    showEmpty(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<NotificationResponse>>> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Error loading notifications", t);
                showError("Lỗi kết nối: " + t.getMessage());
                showEmpty(true);
            }
        });
    }

    private void showCreateNotificationDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_create_notification, null);
        
        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etMessage = dialogView.findViewById(R.id.etMessage);
        Spinner spinnerType = dialogView.findViewById(R.id.spinnerType);
        Spinner spinnerGoal = dialogView.findViewById(R.id.spinnerGoal);

        // Setup spinners
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(), 
                android.R.layout.simple_spinner_item, 
                new String[]{"FOOD", "EXERCISE"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(getContext(), 
                android.R.layout.simple_spinner_item, 
                new String[]{"Tất cả", "WEIGHT_LOSS", "MUSCLE_GAIN", "MAINTAIN_WEIGHT"});
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoal.setAdapter(goalAdapter);

        new AlertDialog.Builder(getContext())
                .setTitle("Tạo thông báo mới")
                .setView(dialogView)
                .setPositiveButton("Gửi", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String message = etMessage.getText().toString().trim();
                    String type = spinnerType.getSelectedItem().toString();
                    String goal = spinnerGoal.getSelectedItemPosition() == 0 ? null : spinnerGoal.getSelectedItem().toString();

                    if (title.isEmpty() || message.isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    sendNotification(title, message, type, goal);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void sendNotification(String title, String message, String type, String goal) {
        SendNotificationRequest request = new SendNotificationRequest(title, message, type, null, goal);

        Call<ApiResponse<Void>> call = (goal == null) ? 
                apiService.sendBroadcastNotification(request) : 
                apiService.sendCustomNotification(request);

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã gửi thông báo thành công", Toast.LENGTH_SHORT).show();
                    loadNotifications(); // Refresh list
                } else {
                    Log.e(TAG, "Failed to send notification: " + response.code());
                    Toast.makeText(getContext(), "Không thể gửi thông báo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e(TAG, "Error sending notification", t);
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markAsRead(Long notificationId) {
        apiService.markNotificationAsRead(notificationId).enqueue(new Callback<ApiResponse<NotificationResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<NotificationResponse>> call, Response<ApiResponse<NotificationResponse>> response) {
                if (response.isSuccessful()) {
                    // Update local list
                    for (NotificationResponse notification : notificationList) {
                        if (notification.getId().equals(notificationId)) {
                            notification.setRead(true);
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<NotificationResponse>> call, Throwable t) {
                Log.e(TAG, "Error marking notification as read", t);
            }
        });
    }

    private void showLoading(boolean show) {
        if (layoutLoading != null) {
            layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void showEmpty(boolean show) {
        if (layoutEmpty != null) {
            layoutEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotifications();
    }
}