
package com.example.app_selfcare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AccountSettingsActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView titleText;
    private LinearLayout notificationLayout, personalInfoLayout, passwordLayout, helpLayout;
    private Button signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings); // phải là đúng XML này

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        personalInfoLayout = findViewById(R.id.personalInfoLayout);
        notificationLayout= findViewById(R.id.notificationLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        helpLayout = findViewById(R.id.helpLayout);
        signOutButton = findViewById(R.id.signOutButton);

        titleText.setText("Cài đặt tài khoản");
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // quay lại trang trước
            }
        });

        personalInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG_CLICK", "Đã nhấn vào Thông tin cá nhân");
                // Mở màn chỉ hiển thị thông tin cá nhân từ backend
                Intent intent = new Intent(AccountSettingsActivity.this, PersonalInfoActivity.class);
                startActivity(intent);
            }
        });

        notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountSettingsActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        passwordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountSettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        helpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountSettingsActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountSettingsActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Bottom Navigation
        View navHome = findViewById(R.id.navHome);
        View navWorkout = findViewById(R.id.navWorkout);
        View navPlanner = findViewById(R.id.navPlanner);
        View navProfile = findViewById(R.id.navProfile);

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(AccountSettingsActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            });
        }

        if (navWorkout != null) {
            navWorkout.setOnClickListener(v -> {
                startActivity(new Intent(AccountSettingsActivity.this, WorkoutActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (navPlanner != null) {
            navPlanner.setOnClickListener(v -> {
                startActivity(new Intent(AccountSettingsActivity.this, RecipeHomeActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                startActivity(new Intent(AccountSettingsActivity.this, ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }
    }

}