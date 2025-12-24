package com.example.app_selfcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.UserProfileResponse;
import com.example.app_selfcare.Data.Model.Response.UserResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.utils.LocaleManager;
import com.example.app_selfcare.utils.ImageUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInfoActivity extends AppCompatActivity {

    private ImageView btnBack, btnEdit, profileImage;
    private TextView tvFullName, tvEmail, tvDateOfBirth, tvGender, tvHeight, tvWeight, tvHealthGoal;
    private String currentAvatarUrl; // Lưu URL ảnh hiện tại

    private ActivityResultLauncher<Intent> editProfileLauncher;

    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleManager localeManager = new LocaleManager(newBase);
        super.attachBaseContext(localeManager.applyLanguage(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_info);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupEditLauncher();
        setupEvents();
        fetchPersonalInfo();
    }

    private void setupEditLauncher() {
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        fetchPersonalInfo();
                    }
                }
        );
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnEdit = findViewById(R.id.btnEdit);
        profileImage = findViewById(R.id.profileImage);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvDateOfBirth = findViewById(R.id.tvDateOfBirth);
        tvGender = findViewById(R.id.tvGender);
        tvHeight = findViewById(R.id.tvHeight);
        tvWeight = findViewById(R.id.tvWeight);
        tvHealthGoal = findViewById(R.id.tvHealthGoal);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditPersonalInfoActivity.class);
            editProfileLauncher.launch(intent);
        });
        
        // Click vào ảnh profile để xem chi tiết
        profileImage.setOnClickListener(v -> showImageDetail());
    }

    private void fetchPersonalInfo() {
        SharedPreferences prefs = getSharedPreferences("APP_DATA", MODE_PRIVATE);
        String token = prefs.getString("TOKEN", null);

        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getUserProfile("Bearer " + token).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().getResult() == null) {
                    Toast.makeText(PersonalInfoActivity.this, "Không thể tải thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserResponse user = response.body().getResult();
                tvFullName.setText(user.getFullName() != null ? user.getFullName() : "-");
                tvEmail.setText(user.getEmail() != null ? user.getEmail() : "-");

                UserProfileResponse profile = user.getUserProfileResponse();
                if (profile != null) {
                    if (profile.getAvatarUrl() != null && !profile.getAvatarUrl().isEmpty()) {
                        currentAvatarUrl = profile.getAvatarUrl(); // Lưu URL ảnh
                        ImageUtils.loadImageSafely(PersonalInfoActivity.this, profile.getAvatarUrl(), profileImage);
                    } else {
                        // Nếu không có avatar URL, hiển thị ảnh mặc định
                        profileImage.setImageResource(R.drawable.ic_proflie);
                    }

                    if (profile.getDateOfBirth() != null) {
                        tvDateOfBirth.setText("Ngày sinh: " + profile.getDateOfBirth());
                    }
                    if (profile.getGender() != null) {
                        tvGender.setText("Giới tính: " + profile.getGender());
                    }
                    if (profile.getHeight() != null) {
                        tvHeight.setText(String.format("Chiều cao: %.0f cm", profile.getHeight()));
                    }
                    if (profile.getWeight() != null) {
                        tvWeight.setText(String.format("Cân nặng: %.0f kg", profile.getWeight()));
                    }
                    if (profile.getHealthGoal() != null) {
                        tvHealthGoal.setText("Mục tiêu sức khỏe: " + profile.getHealthGoal());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Toast.makeText(PersonalInfoActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImageDetail() {
        if (currentAvatarUrl == null || currentAvatarUrl.isEmpty()) {
            Toast.makeText(this, "Không có ảnh để hiển thị", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo dialog để hiển thị ảnh phóng to
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_detail);
        
        // Thiết lập dialog full screen
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, 
                           WindowManager.LayoutParams.MATCH_PARENT);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                           WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        ImageView imageView = dialog.findViewById(R.id.imageViewDetail);
        TextView btnClose = dialog.findViewById(R.id.btnCloseDialog);

        // Load ảnh vào dialog
        ImageUtils.loadImageSafely(this, currentAvatarUrl, imageView, R.drawable.ic_proflie, false);

        // Đóng dialog khi click nút close
        btnClose.setOnClickListener(v -> dialog.dismiss());
        
        // Đóng dialog khi click vào ảnh
        imageView.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}


