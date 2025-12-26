package com.example.app_selfcare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.app_selfcare.Data.Model.Request.UserRegisterRequest;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.UserResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.example.app_selfcare.utils.LocaleManager;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * RegisterActivity - Màn hình đăng ký tài khoản mới
 * 
 * Chức năng chính:
 * - Cho phép người dùng tạo tài khoản mới với email, họ tên, mật khẩu
 * - Validate thông tin đầu vào (email, password, confirm password)
 * - Gọi API đăng ký và xử lý kết quả
 * - Chuyển hướng về màn hình đăng nhập sau khi đăng ký thành công
 */
public class RegisterActivity extends AppCompatActivity {

    // Các trường nhập liệu
    private TextInputEditText edtEmail, edtFullName, edtPassword, edtConfirmPassword;

    /**
     * Áp dụng ngôn ngữ đã lưu trước khi Activity được tạo
     * @param newBase Context gốc
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleManager localeManager = new LocaleManager(newBase);
        super.attachBaseContext(localeManager.applyLanguage(newBase));
    }

    /**
     * Khởi tạo Activity, thiết lập giao diện và các sự kiện
     * @param savedInstanceState Trạng thái đã lưu của Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Bắt buộc chế độ sáng cho màn hình đăng ký
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Ánh xạ các view từ layout
        initViews();
        
        // Thiết lập các sự kiện click
        setupClickListeners();
    }

    /**
     * Ánh xạ các view từ layout XML
     */
    private void initViews() {
        edtEmail = findViewById(R.id.edtEmail);
        edtFullName = findViewById(R.id.edtFullName);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
    }

    /**
     * Thiết lập các sự kiện click cho các button
     */
    private void setupClickListeners() {
        // Sự kiện nhấn nút đăng ký
        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            doRegister();
        });

        // Sự kiện chuyển về màn hình đăng nhập
        TextView txtLoginRedirect = findViewById(R.id.txtLoginRedirect);
        if (txtLoginRedirect != null) {
            txtLoginRedirect.setOnClickListener(v -> {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            });
        }
    }

    /**
     * Thực hiện đăng ký tài khoản
     * - Validate các trường input
     * - Gọi API register
     * - Xử lý response và điều hướng
     */
    private void doRegister() {
        // Lấy dữ liệu từ các trường nhập
        String email = edtEmail.getText().toString().trim();
        String fullName = edtFullName.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Validate: kiểm tra các trường không được rỗng
        if (email.isEmpty() || fullName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate: mật khẩu xác nhận phải khớp
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate: mật khẩu tối thiểu 6 ký tự
        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo request object
        UserRegisterRequest request = new UserRegisterRequest(email, fullName, password);

        // Gọi API đăng ký
        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.register(request).enqueue(new Callback<ApiResponse<UserResponse>>() {
            /**
             * Xử lý khi API trả về response
             */
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserResponse> apiRes = response.body();

                    if (apiRes.getCode() == 200) {
                        // Đăng ký thành công -> chuyển về màn hình đăng nhập
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        // Đăng ký thất bại -> hiển thị message lỗi
                        String message = apiRes.getMessage() != null ? apiRes.getMessage() : "Đăng ký thất bại!";
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Lỗi máy chủ!", Toast.LENGTH_SHORT).show();
                }
            }

            /**
             * Xử lý khi gọi API thất bại (lỗi mạng, timeout,...)
             */
            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Kết nối thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
