package com.example.app_selfcare.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app_selfcare.FoodDetailActivity;
import com.example.app_selfcare.NotificationActivity;
import com.example.app_selfcare.R;
import com.example.app_selfcare.WorkoutDetailActivity;

/**
 * Manager để hiển thị notification bar trong app khi nhận được thông báo real-time
 */
public class InAppNotificationManager {

    private static final long AUTO_DISMISS_DELAY = 5000; // 5 giây

    private final Activity activity;
    private View notificationView;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable dismissRunnable;

    // Data của notification hiện tại
    private String currentType;
    private Long currentTargetId;

    public InAppNotificationManager(Activity activity) {
        this.activity = activity;
    }

    /**
     * Hiển thị notification bar
     */
    public void showNotification(String type, Long targetId, String title, String message) {
        // Chạy trên UI thread
        handler.post(() -> {
            // Dismiss notification cũ nếu có
            dismissNotification(false);

            // Lưu data
            currentType = type;
            currentTargetId = targetId;

            // Inflate layout
            notificationView = LayoutInflater.from(activity)
                    .inflate(R.layout.layout_in_app_notification, null);

            // Setup views
            TextView tvTitle = notificationView.findViewById(R.id.tvNotificationTitle);
            TextView tvMessage = notificationView.findViewById(R.id.tvNotificationMessage);
            ImageView ivIcon = notificationView.findViewById(R.id.ivNotificationIcon);
            ImageButton btnClose = notificationView.findViewById(R.id.btnCloseNotification);

            tvTitle.setText(title);
            tvMessage.setText(message);

            // Set icon dựa trên type
            if ("FOOD".equals(type)) {
                ivIcon.setImageResource(R.drawable.ic_food_placeholder);
                ivIcon.setBackgroundResource(R.drawable.bg_food_notification_icon);
            } else if ("EXERCISE".equals(type)) {
                ivIcon.setImageResource(R.drawable.ic_exercise);
                ivIcon.setBackgroundResource(R.drawable.bg_exercise_notification_icon);
            } else {
                ivIcon.setImageResource(R.drawable.ic_notification);
            }

            // Click vào notification để mở chi tiết
            notificationView.setOnClickListener(v -> {
                openDetail();
                dismissNotification(true);
            });

            // Click close button
            btnClose.setOnClickListener(v -> dismissNotification(true));

            // Thêm vào root view
            ViewGroup rootView = activity.findViewById(android.R.id.content);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            params.gravity = Gravity.TOP;
            params.topMargin = getStatusBarHeight();

            rootView.addView(notificationView, params);

            // Animation slide in
            notificationView.setTranslationY(-200);
            notificationView.setAlpha(0f);
            notificationView.animate()
                    .translationY(0)
                    .alpha(1f)
                    .setDuration(300)
                    .start();

            // Auto dismiss sau 5 giây
            dismissRunnable = () -> dismissNotification(true);
            handler.postDelayed(dismissRunnable, AUTO_DISMISS_DELAY);
        });
    }

    /**
     * Dismiss notification
     */
    public void dismissNotification(boolean animate) {
        if (notificationView == null) return;

        // Cancel auto dismiss
        if (dismissRunnable != null) {
            handler.removeCallbacks(dismissRunnable);
        }

        if (animate) {
            notificationView.animate()
                    .translationY(-200)
                    .alpha(0f)
                    .setDuration(200)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            removeNotificationView();
                        }
                    })
                    .start();
        } else {
            removeNotificationView();
        }
    }

    private void removeNotificationView() {
        if (notificationView != null && notificationView.getParent() != null) {
            ((ViewGroup) notificationView.getParent()).removeView(notificationView);
        }
        notificationView = null;
    }

    /**
     * Mở màn hình chi tiết dựa trên type
     */
    private void openDetail() {
        Intent intent;

        if (currentType == null || currentTargetId == null || currentTargetId == -1) {
            intent = new Intent(activity, NotificationActivity.class);
        } else {
            switch (currentType) {
                case "FOOD":
                    intent = new Intent(activity, FoodDetailActivity.class);
                    intent.putExtra("foodId", currentTargetId.intValue());
                    break;

                case "EXERCISE":
                    intent = new Intent(activity, WorkoutDetailActivity.class);
                    intent.putExtra("exerciseId", currentTargetId.intValue());
                    break;

                default:
                    intent = new Intent(activity, NotificationActivity.class);
                    break;
            }
        }

        activity.startActivity(intent);
    }

    /**
     * Lấy chiều cao status bar
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Cleanup khi activity destroy
     */
    public void destroy() {
        dismissNotification(false);
        handler.removeCallbacksAndMessages(null);
    }
}
