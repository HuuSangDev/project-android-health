package com.SelfCare.SelftCare.Service;

import com.SelfCare.SelftCare.DTO.Response.NotificationResponse;
import com.SelfCare.SelftCare.Entity.Notification;
import com.SelfCare.SelftCare.Enum.Goal;
import com.SelfCare.SelftCare.Enum.NotificationType;
import com.SelfCare.SelftCare.Exception.AppException;
import com.SelfCare.SelftCare.Exception.ErrorCode;
import com.SelfCare.SelftCare.Repository.NotificationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationService {

    NotificationRepository notificationRepository;
    SimpMessagingTemplate messagingTemplate;
    FcmService fcmService;

    /**
     * Tạo và gửi thông báo khi có Food mới - GỬI THEO GOAL
     */
    public void notifyNewFood(Long foodId, String foodName, Goal goal) {
        Notification notification = Notification.builder()
                .title("Món ăn mới")
                .message("Món ăn \"" + foodName + "\" vừa được thêm vào hệ thống")
                .type(NotificationType.FOOD)
                .targetId(foodId)
                .goal(goal)
                .build();

        saveAndBroadcastByGoal(notification, goal);
    }

    /**
     * Tạo và gửi thông báo khi có Exercise mới - GỬI THEO GOAL
     */
    public void notifyNewExercise(Long exerciseId, String exerciseName, Goal goal) {
        Notification notification = Notification.builder()
                .title("Bài tập mới")
                .message("Bài tập \"" + exerciseName + "\" vừa được thêm vào hệ thống")
                .type(NotificationType.EXERCISE)
                .targetId(exerciseId)
                .goal(goal)
                .build();

        saveAndBroadcastByGoal(notification, goal);
    }

    /**
     * Gửi thông báo tùy chỉnh theo goal (Admin dùng)
     */
    public void sendCustomNotification(String title, String message, NotificationType type, Long targetId, Goal goal) {
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .type(type)
                .targetId(targetId)
                .goal(goal)
                .build();

        if (goal != null) {
            saveAndBroadcastByGoal(notification, goal);
        } else {
            // Nếu không có goal, gửi broadcast cho tất cả
            saveAndBroadcastAll(notification);
        }
        log.info("Custom notification sent: {} - {} to goal: {}", title, message, goal);
    }

    /**
     * Gửi thông báo broadcast cho tất cả users
     */
    public void sendBroadcastNotification(String title, String message) {
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .type(NotificationType.FOOD)
                .targetId(null)
                .goal(null)
                .build();

        saveAndBroadcastAll(notification);
        log.info("Broadcast notification sent: {} - {}", title, message);
    }

    /**
     * Lưu notification và gửi theo GOAL cụ thể
     */
    private void saveAndBroadcastByGoal(Notification notification, Goal goal) {
        // 1. Lưu vào database
        Notification saved = notificationRepository.save(notification);
        log.info("Saved notification: {} for goal: {}", saved.getId(), goal);

        // 2. Convert sang Response
        NotificationResponse response = toResponse(saved);

        // 3. Gửi qua WebSocket tới topic theo goal
        // Ví dụ: /topic/notifications/WEIGHT_LOSS
        String topic = "/topic/notifications/" + goal.name();
        messagingTemplate.convertAndSend(topic, response);
        log.info("Broadcasted notification to {}", topic);

        // 4. Gửi FCM Push Notification tới users có goal này
        sendFcmPushByGoal(saved, goal);
    }

    /**
     * Lưu notification và gửi cho TẤT CẢ users (broadcast)
     */
    private void saveAndBroadcastAll(Notification notification) {
        // 1. Lưu vào database
        Notification saved = notificationRepository.save(notification);
        log.info("Saved broadcast notification: {}", saved.getId());

        // 2. Convert sang Response
        NotificationResponse response = toResponse(saved);

        // 3. Gửi qua WebSocket tới tất cả các topic goal
        for (Goal goal : Goal.values()) {
            String topic = "/topic/notifications/" + goal.name();
            messagingTemplate.convertAndSend(topic, response);
        }
        log.info("Broadcasted notification to all goal topics");

        // 4. Gửi FCM Push Notification tới tất cả devices
        sendFcmPushAll(saved);
    }

    /**
     * Gửi FCM Push Notification theo goal
     */
    private void sendFcmPushByGoal(Notification notification, Goal goal) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("type", notification.getType().name());
            data.put("targetId", String.valueOf(notification.getTargetId()));
            data.put("notificationId", String.valueOf(notification.getId()));
            data.put("goal", goal.name());

            fcmService.sendToUsersByGoal(
                    goal,
                    notification.getTitle(),
                    notification.getMessage(),
                    data
            );

            log.info("FCM push sent for notification: {} to goal: {}", notification.getId(), goal);
        } catch (Exception e) {
            log.error("Failed to send FCM push notification: {}", e.getMessage());
        }
    }

    /**
     * Gửi FCM Push Notification cho tất cả
     */
    private void sendFcmPushAll(Notification notification) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("type", notification.getType().name());
            data.put("targetId", String.valueOf(notification.getTargetId()));
            data.put("notificationId", String.valueOf(notification.getId()));

            fcmService.sendToAll(
                    notification.getTitle(),
                    notification.getMessage(),
                    data
            );

            log.info("FCM push broadcast sent for notification: {}", notification.getId());
        } catch (Exception e) {
            log.error("Failed to send FCM push notification: {}", e.getMessage());
        }
    }

    // ==================== READ OPERATIONS ====================

    /**
     * Lấy tất cả thông báo
     */
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy thông báo theo goal
     */
    public List<NotificationResponse> getNotificationsByGoal(Goal goal) {
        return notificationRepository.findByGoalOrGoalIsNullOrderByCreatedAtDesc(goal)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy thông báo chưa đọc
     */
    public List<NotificationResponse> getUnreadNotifications() {
        return notificationRepository.findByIsReadFalseOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Đánh dấu thông báo đã đọc
     */
    public NotificationResponse markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        
        notification.setRead(true);
        Notification saved = notificationRepository.save(notification);
        return toResponse(saved);
    }

    /**
     * Đánh dấu tất cả thông báo đã đọc
     */
    public void markAllAsRead() {
        List<Notification> unread = notificationRepository.findByIsReadFalseOrderByCreatedAtDesc();
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    /**
     * Convert Entity sang Response
     */
    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .targetId(notification.getTargetId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .goal(notification.getGoal())
                .build();
    }
}
