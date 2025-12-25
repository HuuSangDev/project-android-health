package com.example.app_selfcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.app_selfcare.Data.Model.Request.ForgotPasswordRequest;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPassword extends AppCompatActivity {

    private EditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Force light mode for forgot password screen
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);

        edtEmail = findViewById(R.id.edtEmail);

        // Nút Gửi mã → Gọi API forgot-password
        findViewById(R.id.btnSendCode).setOnClickListener(v -> {
            doForgotPassword();
        });

        // Nút "Log in" → Quay lại Login
        findViewById(R.id.tvLogin).setOnClickListener(v -> {
            startActivity(new Intent(ForgetPassword.this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });

        // Nút Back → Quay lại Login
        findViewById(R.id.backButton).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private void doForgotPassword() {
        String email = edtEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
            return;
        }

        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.forgotPassword(request).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiRes = response.body();

                    if (apiRes.getCode() == 200) {
                        Toast.makeText(ForgetPassword.this, apiRes.getMessage(), Toast.LENGTH_SHORT).show();

                        // Chuyển sang trang OTP và truyền email
                        Intent intent = new Intent(ForgetPassword.this, ForgetPassword_confirm.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        String message = apiRes.getMessage() != null ? apiRes.getMessage() : "Gửi mã thất bại!";
                        Toast.makeText(ForgetPassword.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgetPassword.this, "Lỗi máy chủ!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(ForgetPassword.this, "Kết nối thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}