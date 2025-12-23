package com.example.app_selfcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInfoActivity extends AppCompatActivity {

    private ImageView btnBack, profileImage;
    private TextView tvFullName, tvEmail, tvDateOfBirth, tvGender, tvHeight, tvWeight, tvHealthGoal;

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
        setupEvents();
        fetchPersonalInfo();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
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
                        Glide.with(PersonalInfoActivity.this)
                                .load(profile.getAvatarUrl())
                                .placeholder(R.drawable.ic_proflie)
                                .error(R.drawable.ic_proflie)
                                .into(profileImage);
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
}


