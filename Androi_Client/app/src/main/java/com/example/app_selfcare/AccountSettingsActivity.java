package com.example.app_selfcare;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.app_selfcare.utils.LocaleManager;
import com.example.app_selfcare.utils.ThemeManager;

public class AccountSettingsActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView titleText, tvCurrentLanguage;
    private LinearLayout notificationLayout, personalInfoLayout, passwordLayout, helpLayout, languageLayout;
    private Button signOutButton;
    private SwitchCompat darkModeSwitch;
    private ThemeManager themeManager;
    private LocaleManager localeManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleManager localeManager = new LocaleManager(newBase);
        super.attachBaseContext(localeManager.applyLanguage(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themeManager = new ThemeManager(this);
        themeManager.applySavedTheme();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        localeManager = new LocaleManager(this);
        
        initViews();
        setupClickListeners();
        setupDarkModeSwitch();
        updateLanguageDisplay();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        personalInfoLayout = findViewById(R.id.personalInfoLayout);
        notificationLayout = findViewById(R.id.notificationLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        helpLayout = findViewById(R.id.helpLayout);
        languageLayout = findViewById(R.id.languageLayout);
        signOutButton = findViewById(R.id.signOutButton);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        tvCurrentLanguage = findViewById(R.id.tvCurrentLanguage);

        titleText.setText(R.string.account_settings);
    }

    private void updateLanguageDisplay() {
        if (tvCurrentLanguage != null) {
            tvCurrentLanguage.setText(localeManager.getLanguageDisplayName());
        }
    }

    private void setupDarkModeSwitch() {
        if (darkModeSwitch == null) {
            Log.e("AccountSettings", "darkModeSwitch is null!");
            return;
        }
        
        darkModeSwitch.setChecked(themeManager.isDarkModeEnabled());
        
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            themeManager.setDarkModeEnabled(isChecked);
        });
    }

    private void showLanguageDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_language);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        LinearLayout optionVietnamese = dialog.findViewById(R.id.optionVietnamese);
        LinearLayout optionEnglish = dialog.findViewById(R.id.optionEnglish);
        ImageView checkVietnamese = dialog.findViewById(R.id.checkVietnamese);
        ImageView checkEnglish = dialog.findViewById(R.id.checkEnglish);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        // Hiển thị check cho ngôn ngữ hiện tại
        if (localeManager.isEnglish()) {
            checkEnglish.setVisibility(View.VISIBLE);
            checkVietnamese.setVisibility(View.GONE);
        } else {
            checkVietnamese.setVisibility(View.VISIBLE);
            checkEnglish.setVisibility(View.GONE);
        }

        optionVietnamese.setOnClickListener(v -> {
            if (!LocaleManager.LANGUAGE_VIETNAMESE.equals(localeManager.getCurrentLanguage())) {
                localeManager.setLanguage(LocaleManager.LANGUAGE_VIETNAMESE);
                dialog.dismiss();
                restartApp();
            } else {
                dialog.dismiss();
            }
        });

        optionEnglish.setOnClickListener(v -> {
            if (!LocaleManager.LANGUAGE_ENGLISH.equals(localeManager.getCurrentLanguage())) {
                localeManager.setLanguage(LocaleManager.LANGUAGE_ENGLISH);
                dialog.dismiss();
                restartApp();
            } else {
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void restartApp() {
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        personalInfoLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AccountSettingsActivity.this, PersonalInfoActivity.class);
            startActivity(intent);
        });

        notificationLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AccountSettingsActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        passwordLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AccountSettingsActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        helpLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AccountSettingsActivity.this, HelpActivity.class);
            startActivity(intent);
        });

        languageLayout.setOnClickListener(v -> showLanguageDialog());
        
        signOutButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountSettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Bottom Navigation
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        View navHome = findViewById(R.id.navHome);
        View navWorkout = findViewById(R.id.navWorkout);
        View navPlanner = findViewById(R.id.navPlanner);
        View navProfile = findViewById(R.id.navProfile);

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            });
        }

        if (navWorkout != null) {
            navWorkout.setOnClickListener(v -> {
                startActivity(new Intent(this, WorkoutActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (navPlanner != null) {
            navPlanner.setOnClickListener(v -> {
                startActivity(new Intent(this, RecipeHomeActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }
    }
}
