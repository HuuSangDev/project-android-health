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
    import com.example.app_selfcare.upload.InforSex;
    import com.example.app_selfcare.utils.LocaleManager;
    import com.google.android.material.textfield.TextInputEditText;

    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;

    public class RegisterActivity extends AppCompatActivity {

        private TextInputEditText edtEmail, edtFullName, edtPassword, edtConfirmPassword;

        @Override
        protected void attachBaseContext(Context newBase) {
            LocaleManager localeManager = new LocaleManager(newBase);
            super.attachBaseContext(localeManager.applyLanguage(newBase));
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            // Force light mode for register screen only (not affecting other activities)
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_register);

            // Khởi tạo các EditText
            edtEmail = findViewById(R.id.edtEmail);
            edtFullName = findViewById(R.id.edtFullName);
            edtPassword = findViewById(R.id.edtPassword);
            edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

            // Nút Đăng ký → Gọi API đăng ký
            findViewById(R.id.btnRegister).setOnClickListener(v -> {
                doRegister();
            });

            // Nút "Đăng nhập" → Quay lại Login
            TextView txtLoginRedirect = findViewById(R.id.txtLoginRedirect);
            if (txtLoginRedirect != null) {
                txtLoginRedirect.setOnClickListener(v -> {
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                });
            }
        }

        private void doRegister() {
            String email = edtEmail.getText().toString().trim();
            String fullName = edtFullName.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            // Validation
            if (email.isEmpty() || fullName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo request
            UserRegisterRequest request = new UserRegisterRequest(email, fullName, password);

            // Gọi API
            ApiService api = ApiClient.getClient().create(ApiService.class);

            api.register(request).enqueue(new Callback<ApiResponse<UserResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<UserResponse> apiRes = response.body();

                        if (apiRes.getCode() == 200) {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                            // Chuyển đến trang InforSex
                            Intent intent = new Intent(RegisterActivity.this, InforSex.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        } else {
                            String message = apiRes.getMessage() != null ? apiRes.getMessage() : "Đăng ký thất bại!";
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Lỗi máy chủ!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Kết nối thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }