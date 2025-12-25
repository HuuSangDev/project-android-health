// NotificationActivity.java
package com.example.app_selfcare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_selfcare.utils.LocaleManager;

public class NotificationActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView titleText;
    private LinearLayout personalNotifLayout, waterNotifLayout, exerciseNotifLayout, foodNotifLayout;
    private Switch personalNotifSwitch, waterNotifSwitch, exerciseNotifSwitch, foodNotifSwitch;
    private TextView personalNotifDesc, waterNotifDesc, exerciseNotifDesc, foodNotifDesc;

    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleManager localeManager = new LocaleManager(newBase);
        super.attachBaseContext(localeManager.applyLanguage(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        initViews();
        setupClickListeners();
        setupNotificationSettings();
    }


    private void initViews() {
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);

        personalNotifLayout = findViewById(R.id.personalNotifLayout);
        waterNotifLayout = findViewById(R.id.waterNotifLayout);
        exerciseNotifLayout = findViewById(R.id.exerciseNotifLayout);
        foodNotifLayout = findViewById(R.id.foodNotifLayout);

        personalNotifSwitch = findViewById(R.id.personalNotifSwitch);
        waterNotifSwitch = findViewById(R.id.waterNotifSwitch);
        exerciseNotifSwitch = findViewById(R.id.exerciseNotifSwitch);
        foodNotifSwitch = findViewById(R.id.foodNotifSwitch);

        personalNotifDesc = findViewById(R.id.personalNotifDesc);
        waterNotifDesc = findViewById(R.id.waterNotifDesc);
        exerciseNotifDesc = findViewById(R.id.exerciseNotifDesc);
        foodNotifDesc = findViewById(R.id.foodNotifDesc);

        titleText.setText(R.string.notifications);
    }

    private void setupNotificationSettings() {
        // Set notification descriptions - using string resources
        personalNotifDesc.setText(R.string.personal_points_desc);
        waterNotifDesc.setText(R.string.water_notification_desc);
        exerciseNotifDesc.setText(R.string.exercise_notification_desc);
        foodNotifDesc.setText(R.string.meal_notification_desc);

        // Set default switch states
        personalNotifSwitch.setChecked(true);
        waterNotifSwitch.setChecked(false);
        exerciseNotifSwitch.setChecked(false);
        foodNotifSwitch.setChecked(false);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        personalNotifSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle personal notification toggle
        });

        waterNotifSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle water notification toggle
        });

        exerciseNotifSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle exercise notification toggle
        });

        foodNotifSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle food notification toggle
        });

        personalNotifLayout.setOnClickListener(v -> personalNotifSwitch.toggle());
        waterNotifLayout.setOnClickListener(v -> waterNotifSwitch.toggle());
        exerciseNotifLayout.setOnClickListener(v -> exerciseNotifSwitch.toggle());
        foodNotifLayout.setOnClickListener(v -> foodNotifSwitch.toggle());

        // Bottom Navigation
        View navHome = findViewById(R.id.navHome);
        View navWorkout = findViewById(R.id.navWorkout);
        View navPlanner = findViewById(R.id.navPlanner);
        View navProfile = findViewById(R.id.navProfile);

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(NotificationActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            });
        }

        if (navWorkout != null) {
            navWorkout.setOnClickListener(v -> {
                startActivity(new Intent(NotificationActivity.this, WorkoutActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (navPlanner != null) {
            navPlanner.setOnClickListener(v -> {
                startActivity(new Intent(NotificationActivity.this, RecipeHomeActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                startActivity(new Intent(NotificationActivity.this, ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }
    }


}