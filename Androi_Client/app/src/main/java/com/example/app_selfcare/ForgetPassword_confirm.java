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
import com.example.app_selfcare.Data.Model.Request.VerifyOtpRequest;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPassword_confirm extends AppCompatActivity {

    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private TextView tvDescription;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Force light mode for OTP verification screen
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password_confirm);

        // Lấy email từ Intent
        email = getIntent().getStringExtra("email");
        if (email == null) {
            email = "";
        }

        // Khởi tạo các EditText OTP
        otp1 = findViewById(R.id.otp_edit_text1);
        otp2 = findViewById(R.id.otp_edit_text2);
        otp3 = findViewById(R.id.otp_edit_text3);
        otp4 = findViewById(R.id.otp_edit_text4);
        otp5 = findViewById(R.id.otp_edit_text5);
        otp6 = findViewById(R.id.otp_edit_text6);
        tvDescription = findViewById(R.id.tvDescription);

        // Cập nhật mô tả với email
        if (!email.isEmpty()) {
            tvDescription.setText("Chúng tôi đã gửi mã tới " + email);
        }

        // Nút Xác nhận → Gọi API verify-otp
        findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            doVerifyOtp();
        });

        // Nút "Gửi lại mã" → Gửi lại OTP
        findViewById(R.id.tvResendCode).setOnClickListener(v -> {
            doResendOtp();
        });

        // Nút Back → Quay lại trang nhập email
        findViewById(R.id.backButton).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private void doVerifyOtp() {
        // Lấy OTP từ 6 ô
        String otp = otp1.getText().toString() +
                     otp2.getText().toString() +
                     otp3.getText().toString() +
                     otp4.getText().toString() +
                     otp5.getText().toString() +
                     otp6.getText().toString();

        if (otp.length() != 6) {
            Toast.makeText(this, "Vui lòng nhập đủ 6 số OTP!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy email!", Toast.LENGTH_SHORT).show();
            return;
        }

        VerifyOtpRequest request = new VerifyOtpRequest(email, otp);

        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.verifyOtp(request).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiRes = response.body();

                    if (apiRes.getCode() == 200) {
                        Toast.makeText(ForgetPassword_confirm.this, apiRes.getResult(), Toast.LENGTH_SHORT).show();

                        // Chuyển sang trang đổi mật khẩu và truyền email
                        Intent intent = new Intent(ForgetPassword_confirm.this, ResetPasswordActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        String message = apiRes.getMessage() != null ? apiRes.getMessage() : "Xác minh OTP thất bại!";
                        Toast.makeText(ForgetPassword_confirm.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgetPassword_confirm.this, "Lỗi máy chủ!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(ForgetPassword_confirm.this, "Kết nối thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doResendOtp() {
        if (email.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy email!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ForgetPassword_confirm.this, "Đã gửi lại mã OTP!", Toast.LENGTH_SHORT).show();
                        // Xóa các ô OTP
                        otp1.setText("");
                        otp2.setText("");
                        otp3.setText("");
                        otp4.setText("");
                        otp5.setText("");
                        otp6.setText("");
                    } else {
                        String message = apiRes.getMessage() != null ? apiRes.getMessage() : "Gửi lại mã thất bại!";
                        Toast.makeText(ForgetPassword_confirm.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForgetPassword_confirm.this, "Lỗi máy chủ!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(ForgetPassword_confirm.this, "Kết nối thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}