package com.SelfCare.SelftCare.Service;

import com.SelfCare.SelftCare.Enum.Goal;
import com.SelfCare.SelftCare.Repository.DeviceTokenRepository;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;
    private final DeviceTokenRepository deviceTokenRepository;

    /**
     * Gửi push notification tới danh sách tokens
     * @param tokens Danh sách FCM tokens
     * @param title Tiêu đề thông báo
     * @param body Nội dung thông báo
     * @param data Data payload (để Android xử lý khi click)
     */
    @Transactional
    public void sendToTokens(List<String> tokens, String title, String body, Map<String, String> data) {
        if (firebaseMessaging == null) {
            log.warn("FirebaseMessaging not initialized. Skipping push notification.");
            return;
        }

        if (tokens == null || tokens.isEmpty()) {
            log.info("No tokens to send push notification");
            return;
        }

        try {
            // Tạo notification payload
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            // Tạo Android config
            AndroidConfig androidConfig = AndroidConfig.builder()
                    .setNotification(AndroidNotification.builder()
                            .setIcon("ic_notification")
                            .setColor("#4CAF50")
                            .setSound("default")
                            .setClickAction("OPEN_NOTIFICATION")
                            .build())
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .build();

            // Tạo message cho multicast
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(notification)
                    .setAndroidConfig(androidConfig)
                    .putAllData(data != null ? data : Map.of())
                    .addAllTokens(tokens)
                    .build();

            // Gửi multicast
            BatchResponse response = firebaseMessaging.sendEachForMulticast(message);

            log.info("FCM sent: {} success, {} failure out of {} tokens",
                    response.getSuccessCount(),
                    response.getFailureCount(),
                    tokens.size());

            // Xử lý các token lỗi
            handleFailedTokens(tokens, response);

        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM notification: {}", e.getMessage());
        }
    }

    /**
     * Gửi push notification tới một token
     */
    public void sendToToken(String token, String title, String body, Map<String, String> data) {
        sendToTokens(List.of(token), title, body, data);
    }

    /**
     * Gửi push notification tới tất cả users (broadcast)
     */
    @Transactional
    public void sendToAll(String title, String body, Map<String, String> data) {
        List<String> allTokens = deviceTokenRepository.findAllActiveTokenStrings();
        
        if (allTokens.isEmpty()) {
            log.info("No active tokens found for broadcast");
            return;
        }

        // FCM giới hạn 500 tokens mỗi lần gửi multicast
        int batchSize = 500;
        for (int i = 0; i < allTokens.size(); i += batchSize) {
            List<String> batch = allTokens.subList(i, Math.min(i + batchSize, allTokens.size()));
            sendToTokens(batch, title, body, data);
        }
    }

    /**
     * Gửi push notification tới users có goal cụ thể
     */
    @Transactional
    public void sendToUsersByGoal(Goal goal, String title, String body, Map<String, String> data) {
        if (goal == null) {
            log.warn("Goal is null, skipping FCM push");
            return;
        }

        List<String> tokens = deviceTokenRepository.findActiveTokenStringsByGoal(goal);
        
        if (tokens.isEmpty()) {
            log.info("No active tokens found for goal: {}", goal);
            return;
        }

        log.info("Sending FCM to {} tokens with goal: {}", tokens.size(), goal);

        // FCM giới hạn 500 tokens mỗi lần gửi multicast
        int batchSize = 500;
        for (int i = 0; i < tokens.size(); i += batchSize) {
            List<String> batch = tokens.subList(i, Math.min(i + batchSize, tokens.size()));
            sendToTokens(batch, title, body, data);
        }
    }

    /**
     * Gửi push notification tới một user cụ thể
     */
    @Transactional
    public void sendToUser(Long userId, String title, String body, Map<String, String> data) {
        List<String> userTokens = deviceTokenRepository.findActiveTokenStringsByUserId(userId);
        
        if (userTokens.isEmpty()) {
            log.info("No active tokens found for user: {}", userId);
            return;
        }

        sendToTokens(userTokens, title, body, data);
    }

    /**
     * Xử lý các token lỗi (invalid/unregistered)
     * Deactivate các token không còn hợp lệ
     */
    private void handleFailedTokens(List<String> tokens, BatchResponse response) {
        List<String> invalidTokens = new ArrayList<>();

        List<SendResponse> responses = response.getResponses();
        for (int i = 0; i < responses.size(); i++) {
            SendResponse sendResponse = responses.get(i);
            if (!sendResponse.isSuccessful()) {
                FirebaseMessagingException exception = sendResponse.getException();
                if (exception != null) {
                    MessagingErrorCode errorCode = exception.getMessagingErrorCode();
                    
                    // Các error code cho biết token không còn hợp lệ
                    if (errorCode == MessagingErrorCode.UNREGISTERED ||
                        errorCode == MessagingErrorCode.INVALID_ARGUMENT) {
                        
                        String invalidToken = tokens.get(i);
                        invalidTokens.add(invalidToken);
                        log.warn("Invalid FCM token detected: {} - Error: {}", 
                                invalidToken.substring(0, Math.min(20, invalidToken.length())) + "...",
                                errorCode);
                    }
                }
            }
        }

        // Deactivate các token không hợp lệ
        if (!invalidTokens.isEmpty()) {
            deviceTokenRepository.deactivateTokens(invalidTokens);
            log.info("Deactivated {} invalid tokens", invalidTokens.size());
        }
    }
}
