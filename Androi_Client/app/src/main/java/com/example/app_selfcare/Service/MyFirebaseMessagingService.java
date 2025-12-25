package com.example.app_selfcare.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.app_selfcare.NotificationActivity;
import com.example.app_selfcare.R;
import com.example.app_selfcare.RecipeDetailActivity;
import com.example.app_selfcare.WorkoutDetailActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "selfcare_notifications";
    private static final String CHANNEL_NAME = "SelfCare Notifications";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Lấy data payload
        Map<String, String> data = remoteMessage.getData();
        
        String title = "";
        String body = "";
        String type = data.get("type");
        String targetId = data.get("targetId");

        // Lấy notification payload (nếu có)
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Notification Title: " + title);
            Log.d(TAG, "Notification Body: " + body);
        }

        // Nếu không có notification payload, lấy từ data
        if (title == null || title.isEmpty()) {
            title = data.get("title");
        }
        if (body == null || body.isEmpty()) {
            body = data.get("body");
        }

        // Hiển thị notification
        if (title != null && body != null) {
            sendNotification(title, body, type, targetId);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM Token: " + token);

        // Lưu token vào SharedPreferences
        saveFcmToken(token);

        // Gửi token lên server (nếu đã đăng nhập)
        sendTokenToServer(token);
    }

    /**
     * Lưu FCM token vào SharedPreferences
     */
    private void saveFcmToken(String token) {
        SharedPreferences prefs = getSharedPreferences("APP_DATA", MODE_PRIVATE);
        prefs.edit().putString("FCM_TOKEN", token).apply();
        Log.d(TAG, "FCM Token saved to SharedPreferences");
    }

    /**
     * Gửi token lên server
     * Sẽ được gọi khi có token mới hoặc sau khi login
     */
    private void sendTokenToServer(String token) {
        // Kiểm tra đã đăng nhập chưa
        SharedPreferences prefs = getSharedPreferences("APP_DATA", MODE_PRIVATE);
        String authToken = prefs.getString("TOKEN", null);

        if (authToken == null || authToken.isEmpty()) {
            Log.d(TAG, "User not logged in, skip sending FCM token to server");
            return;
        }

        // Token sẽ được gửi lên server thông qua FcmTokenManager
        // Việc này sẽ được xử lý trong LoginActivity sau khi login thành công
        Log.d(TAG, "FCM token ready to send to server");
    }

    /**
     * Hiển thị notification
     */
    private void sendNotification(String title, String body, String type, String targetId) {
        // Tạo intent dựa trên type
        Intent intent = createIntent(type, targetId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Tạo notification channel cho Android O trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Thông báo từ SelfCare");
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        // Hiển thị notification với ID unique
        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    /**
     * Tạo Intent dựa trên type notification
     */
    private Intent createIntent(String type, String targetId) {
        Intent intent;

        if (type == null || targetId == null) {
            // Mặc định mở NotificationActivity
            intent = new Intent(this, NotificationActivity.class);
        } else {
            switch (type) {
                case "FOOD":
                    intent = new Intent(this, RecipeDetailActivity.class);
                    try {
                        intent.putExtra("foodId", Integer.parseInt(targetId));
                    } catch (NumberFormatException e) {
                        intent = new Intent(this, NotificationActivity.class);
                    }
                    break;

                case "EXERCISE":
                    intent = new Intent(this, WorkoutDetailActivity.class);
                    try {
                        intent.putExtra("exerciseId", Integer.parseInt(targetId));
                    } catch (NumberFormatException e) {
                        intent = new Intent(this, NotificationActivity.class);
                    }
                    break;

                default:
                    intent = new Intent(this, NotificationActivity.class);
                    break;
            }
        }

        return intent;
    }
}
