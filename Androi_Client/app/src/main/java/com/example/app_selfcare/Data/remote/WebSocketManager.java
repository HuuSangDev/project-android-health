package com.example.app_selfcare.Data.remote;

import android.util.Log;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;

/**
 * Singleton class để quản lý kết nối WebSocket STOMP
 */
public class WebSocketManager {

    private static final String TAG = "WebSocketManager";
//    private static final String WS_URL = "ws://13.214.39.228:8080/ws/websocket";
    private static final String WS_URL = "ws://10.0.2.2:8080/ws/websocket"; // Emulator


    private static WebSocketManager instance;
    private StompClient stompClient;
    private CompositeDisposable compositeDisposable;
    private NotificationListener notificationListener;

    private boolean isConnected = false;

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

    public void connect() {
        if (stompClient != null && stompClient.isConnected()) {
            Log.d(TAG, "Already connected");
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
                            subscribeToNotifications();
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

    private void subscribeToNotifications() {
        if (stompClient == null || !stompClient.isConnected()) {
            Log.w(TAG, "Cannot subscribe - not connected");
            return;
        }

        Disposable topicDisposable = stompClient.topic("/topic/notifications")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    String payload = topicMessage.getPayload();
                    Log.d(TAG, "Received notification: " + payload);
                    parseAndNotify(payload);
                }, throwable -> {
                    Log.e(TAG, "Error subscribing to topic", throwable);
                });

        compositeDisposable.add(topicDisposable);
        Log.d(TAG, "Subscribed to /topic/notifications");
    }

    private void parseAndNotify(String json) {
        try {
            // Parse JSON manually (hoặc dùng Gson)
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
        if (stompClient != null) {
            stompClient.disconnect();
        }
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
        isConnected = false;
        Log.d(TAG, "Disconnected");
    }

    public boolean isConnected() {
        return isConnected && stompClient != null && stompClient.isConnected();
    }
}
