package com.example.app_selfcare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app_selfcare.Data.Model.Request.UserLoginRequest;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.UserLoginResponse;
import com.example.app_selfcare.Data.Model.Response.UserProfileResponse;
import com.example.app_selfcare.Data.Model.Response.UserResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;

import com.example.app_selfcare.Ui.Admin.AdminHomeActivity;
import com.example.app_selfcare.upload.InforSex;
import com.example.app_selfcare.utils.FcmTokenManager;
import com.example.app_selfcare.utils.LocaleManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;

    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleManager localeManager = new LocaleManager(newBase);
        super.attachBaseContext(localeManager.applyLanguage(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Force light mode for login screen only (not affecting other activities)
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tranglogin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Chuyển sang Register
        findViewById(R.id.txtRegister).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Chuyển sang Forgot Password
        findViewById(R.id.txtForgotPassword).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgetPassword.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // NÚT LOGIN
        btnLogin.setOnClickListener(v -> {
            doLogin();
        });
    }

    private void doLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        UserLoginRequest request = new UserLoginRequest(email, password);
        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.login(request).enqueue(new Callback<ApiResponse<UserLoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserLoginResponse>> call, Response<ApiResponse<UserLoginResponse>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<UserLoginResponse> apiRes = response.body();

                    if (apiRes.getCode() == 200 && apiRes.getResult().isAuthenticated()) {

                        // Lấy token
                        String token = apiRes.getResult().getToken();
                        String role  = apiRes.getResult().getRole();

                        // Lưu token vào SharedPreferences
                        saveToken(token);

                        // Đăng ký FCM token sau khi login thành công
                        registerFcmToken();

                        if (role == null) {
                            Toast.makeText(LoginActivity.this, "Không tìm thấy role!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if ("ADMIN".equalsIgnoreCase(role))
                        {
                            Intent intent= new Intent(LoginActivity.this, AdminHomeActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            checkUserProfile(token);
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, "Sai email hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Lỗi máy chủ!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserLoginResponse>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Kết nối thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToken(String token) {
        getSharedPreferences("APP_DATA", MODE_PRIVATE)
                .edit()
                .putString("TOKEN", token)
                .apply();
    }

    private void checkUserProfile(String token) {

        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.getUserProfile("Bearer " + token).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    goToInforSex();
                    return;
                }

                UserResponse user = response.body().getResult();

                if (user == null || user.getUserProfileResponse() == null) {
                    goToInforSex();
                    return;
                }

                UserProfileResponse profile = user.getUserProfileResponse();

                boolean isProfileEmpty =
                        profile.getAvatarUrl() == null &&
                                profile.getDateOfBirth() == null &&
                                profile.getGender() == null &&
                                profile.getHeight() == null &&
                                profile.getWeight() == null &&
                                profile.getHealthGoal() == null;

                if (isProfileEmpty) {
                    // Chưa tạo profile
                    goToInforSex();
                } else {
                    // Đã có profile
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                goToInforSex();
            }
        });
    }

    private void goToInforSex() {
        Intent intent = new Intent(LoginActivity.this, InforSex.class);
        startActivity(intent);
        finish();
    }

    /**
     * Đăng ký FCM token sau khi login thành công
     */
    private void registerFcmToken() {
        FcmTokenManager fcmTokenManager = new FcmTokenManager(this);
        fcmTokenManager.registerToken();
    }
}
