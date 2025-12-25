package com.example.app_selfcare;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.app_selfcare.Data.Model.Request.ResetPasswordRequest;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText edtNewPassword, edtConfirmPassword;
    Button btnChangePassword;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Force light mode for reset password screen
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        
        setContentView(R.layout.activity_reset_password);

        // Lấy email từ Intent
        email = getIntent().getStringExtra("email");
        if (email == null) {
            email = "";
        }

        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        // Nút đổi mật khẩu → Gọi API reset-password
        btnChangePassword.setOnClickListener(v -> {
            doResetPassword();
        });

        // Nút Back → Quay lại OTP
        findViewById(R.id.backButton).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private void doResetPassword() {
        String pass1 = edtNewPassword.getText().toString().trim();
        String pass2 = edtConfirmPassword.getText().toString().trim();

        if (pass1.isEmpty() || pass2.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass1.equals(pass2)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass1.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy email!", Toast.LENGTH_SHORT).show();
            return;
        }

        ResetPasswordRequest request = new ResetPasswordRequest(email, pass1);

        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.resetPassword(request).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiRes = response.body();

                    if (apiRes.getCode() == 200) {
                        Toast.makeText(ResetPasswordActivity.this, apiRes.getResult(), Toast.LENGTH_SHORT).show();
                        showSuccessDialog();
                    } else {
                        String message = apiRes.getMessage() != null ? apiRes.getMessage() : "Đặt lại mật khẩu thất bại!";
                        Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Lỗi máy chủ!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Kết nối thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_password_success, null);
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();

        Button btnToLogin = view.findViewById(R.id.btnToLogin);
        btnToLogin.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finishAffinity(); // Đóng toàn bộ stack quên mật khẩu
        });

        dialog.show();
    }
}