package com.example.app_selfcare.Data.remote;

import android.util.Log;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

/**
 * Singleton class để quản lý kết nối WebSocket STOMP
 * Subscribe theo goal của user để nhận notification phù hợp
 */
public class WebSocketManager {

    private static final String TAG = "WebSocketManager";
    
    // Local Emulator - Native WebSocket (không có SockJS)
    private static final String WS_URL = "ws://10.0.2.2:8080/ws";
    // AWS EC2: private static final String WS_URL = "ws://13.214.39.228:8080/ws";

    private static WebSocketManager instance;
    private StompClient stompClient;
    private CompositeDisposable compositeDisposable;
    private NotificationListener notificationListener;
    private Disposable topicDisposable;

    private boolean isConnected = false;
    private String currentGoal = null; // Goal hiện tại đang subscribe

    public interface NotificationListener {
        void onNotificationReceived(String type, Long targetId, String title, String message);
        void onConnectionStateChanged(boolean connected);
    }

    private WebSocketManager() {
        compositeDisposable = new CompositeDisposable();
    }

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    public void setNotificationListener(NotificationListener listener) {
        this.notificationListener = listener;
    }

    /**
     * Kết nối WebSocket và subscribe theo goal
     * @param goal Goal của user (WEIGHT_LOSS, MAINTAIN, WEIGHT_GAIN)
     */
    public void connect(String goal) {
        this.currentGoal = goal;

        if (stompClient != null && stompClient.isConnected()) {
            Log.d(TAG, "Already connected, resubscribing to goal: " + goal);
            // Nếu đã kết nối, chỉ cần resubscribe
            unsubscribeCurrentTopic();
            subscribeToNotifications(goal);
            return;
        }

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WS_URL);
        stompClient.withClientHeartbeat(10000).withServerHeartbeat(10000);

        // Lifecycle events
        Disposable lifecycleDisposable = stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG, "WebSocket OPENED");
                            isConnected = true;
                            if (notificationListener != null) {
                                notificationListener.onConnectionStateChanged(true);
                            }
                            // Subscribe theo goal sau khi kết nối
                            subscribeToNotifications(currentGoal);
                            break;
                        case ERROR:
                            Log.e(TAG, "WebSocket ERROR", lifecycleEvent.getException());
                            isConnected = false;
                            if (notificationListener != null) {
                                notificationListener.onConnectionStateChanged(false);
                            }
                            break;
                        case CLOSED:
                            Log.d(TAG, "WebSocket CLOSED");
                            isConnected = false;
                            if (notificationListener != null) {
                                notificationListener.onConnectionStateChanged(false);
                            }
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.w(TAG, "WebSocket FAILED_SERVER_HEARTBEAT");
                            break;
                    }
                }, throwable -> {
                    Log.e(TAG, "Lifecycle error", throwable);
                });

        compositeDisposable.add(lifecycleDisposable);
        stompClient.connect();
    }

    /**
     * Kết nối không có goal (fallback, không khuyến khích)
     */
    public void connect() {
        connect(null);
    }

    /**
     * Hủy subscribe topic hiện tại
     */
    private void unsubscribeCurrentTopic() {
        if (topicDisposable != null && !topicDisposable.isDisposed()) {
            topicDisposable.dispose();
            topicDisposable = null;
            Log.d(TAG, "Unsubscribed from current topic");
        }
    }

    /**
     * Subscribe tới topic theo goal
     */
    private void subscribeToNotifications(String goal) {
        if (stompClient == null || !stompClient.isConnected()) {
            Log.w(TAG, "Cannot subscribe - not connected");
            return;
        }

        // Hủy subscription cũ nếu có
        unsubscribeCurrentTopic();

        // Xác định topic dựa trên goal
        String topic;
        if (goal != null && !goal.isEmpty()) {
            topic = "/topic/notifications/" + goal;
        } else {
            // Fallback: subscribe tất cả (không khuyến khích)
            topic = "/topic/notifications/WEIGHT_LOSS"; // Default
            Log.w(TAG, "No goal provided, using default topic");
        }

        topicDisposable = stompClient.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    String payload = topicMessage.getPayload();
                    Log.d(TAG, "Received notification from " + topic + ": " + payload);
                    parseAndNotify(payload);
                }, throwable -> {
                    Log.e(TAG, "Error subscribing to topic: " + topic, throwable);
                });

        compositeDisposable.add(topicDisposable);
        Log.d(TAG, "Subscribed to " + topic);
    }

    /**
     * Thay đổi goal và resubscribe
     */
    public void changeGoal(String newGoal) {
        if (newGoal == null || newGoal.equals(currentGoal)) {
            return;
        }

        Log.d(TAG, "Changing goal from " + currentGoal + " to " + newGoal);
        currentGoal = newGoal;

        if (isConnected) {
            unsubscribeCurrentTopic();
            subscribeToNotifications(newGoal);
        }
    }

    private void parseAndNotify(String json) {
        try {
            org.json.JSONObject obj = new org.json.JSONObject(json);
            String type = obj.optString("type", "");
            Long targetId = obj.optLong("targetId", -1);
            String title = obj.optString("title", "Thông báo mới");
            String message = obj.optString("message", "");

            if (notificationListener != null) {
                notificationListener.onNotificationReceived(type, targetId, title, message);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing notification JSON", e);
        }
    }

    public void disconnect() {
        unsubscribeCurrentTopic();
        if (stompClient != null) {
            stompClient.disconnect();
        }
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
        isConnected = false;
        currentGoal = null;
        Log.d(TAG, "Disconnected");
    }

    public boolean isConnected() {
        return isConnected && stompClient != null && stompClient.isConnected();
    }

    public String getCurrentGoal() {
        return currentGoal;
    }
}
