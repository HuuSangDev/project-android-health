package com.example.app_selfcare.Ui.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.app_selfcare.LoginActivity;
import com.example.app_selfcare.R;
import com.example.app_selfcare.Fragment.*;
import com.google.android.material.navigation.NavigationView;

public class AdminHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Initialize views
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        // Setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Dashboard");
        }

        // Setup navigation drawer
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Load default fragment (Dashboard)
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
            navigationView.setCheckedItem(R.id.nav_statistics); // ⭐ SỬA ĐÂY
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        String title = "";

        int itemId = item.getItemId();

        // ⭐ THÊM CASE DASHBOARD
        if (itemId == R.id.nav_statistics) {
            fragment = new StatisticsFragment();
            title = "Thống kê";
        } else if (itemId == R.id.nav_exercises) {
            fragment = new ExercisesFragment();
            title = "Quản lí Bài tập";
        } else if (itemId == R.id.nav_foods) {
            fragment = new FoodsFragment();
            title = "Quản lí Món ăn";
        } else if (itemId == R.id.nav_food_categories) {
            fragment = new FoodCategoriesFragment();
            title = "Danh mục Món ăn";
        } else if (itemId == R.id.nav_exercise_categories) {
            fragment = new ExerciseCategoriesFragment();
            title = "Danh mục Bài tập";
        } else if (itemId == R.id.nav_users) {
            fragment = new UsersFragment();
            title = "Quản lí Người dùng";
        } else if (itemId == R.id.nav_notifications) {
            fragment = new NotificationsFragment();
            title = "Thông báo";
        } else if (itemId == R.id.nav_logout) {
            showLogoutDialog();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        if (fragment != null) {
            loadFragment(fragment);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminHomeActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}