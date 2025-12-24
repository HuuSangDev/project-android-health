package com.SelfCare.SelftCare.Service;

import com.SelfCare.SelftCare.DTO.Response.NotificationResponse;
import com.SelfCare.SelftCare.Entity.Notification;
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
     * Tạo và gửi thông báo khi có Food mới
     */
    public void notifyNewFood(Long foodId, String foodName) {
        Notification notification = Notification.builder()
                .title("Món ăn mới")
                .message("Món ăn \"" + foodName + "\" vừa được thêm vào hệ thống")
                .type(NotificationType.FOOD)
                .targetId(foodId)
                .build();

        saveAndBroadcast(notification);
    }

    /**
     * Tạo và gửi thông báo khi có Exercise mới
     */
    public void notifyNewExercise(Long exerciseId, String exerciseName) {
        Notification notification = Notification.builder()
                .title("Bài tập mới")
                .message("Bài tập \"" + exerciseName + "\" vừa được thêm vào hệ thống")
                .type(NotificationType.EXERCISE)
                .targetId(exerciseId)
                .build();

        saveAndBroadcast(notification);
    }

    /**
     * Gửi thông báo tùy chỉnh (Admin dùng)
     * @param title Tiêu đề thông báo
     * @param message Nội dung thông báo
     * @param type Loại: FOOD hoặc EXERCISE
     * @param targetId ID của food/exercise (có thể null nếu thông báo chung)
     */
    public void sendCustomNotification(String title, String message, NotificationType type, Long targetId) {
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .type(type)
                .targetId(targetId)
                .build();

        saveAndBroadcast(notification);
        log.info("Custom notification sent: {} - {}", title, message);
    }

    /**
     * Gửi thông báo chung (không liên kết food/exercise cụ thể)
     */
    public void sendBroadcastNotification(String title, String message) {
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .type(NotificationType.FOOD) // Default type
                .targetId(null)
                .build();

        saveAndBroadcast(notification);
        log.info("Broadcast notification sent: {} - {}", title, message);
    }

    /**
     * Lưu notification vào DB, gửi qua WebSocket và FCM Push
     */
    private void saveAndBroadcast(Notification notification) {
        // 1. Lưu vào database
        Notification saved = notificationRepository.save(notification);
        log.info("Saved notification: {}", saved.getId());

        // 2. Convert sang Response
        NotificationResponse response = toResponse(saved);

        // 3. Gửi qua WebSocket tới topic /topic/notifications
        messagingTemplate.convertAndSend("/topic/notifications", response);
        log.info("Broadcasted notification to /topic/notifications");

        // 4. Gửi FCM Push Notification tới tất cả devices
        sendFcmPush(saved);
    }

    /**
     * Gửi FCM Push Notification
     */
    private void sendFcmPush(Notification notification) {
        try {
            // Tạo data payload để Android xử lý khi click
            Map<String, String> data = new HashMap<>();
            data.put("type", notification.getType().name());
            data.put("targetId", String.valueOf(notification.getTargetId()));
            data.put("notificationId", String.valueOf(notification.getId()));

            // Gửi broadcast tới tất cả active tokens
            fcmService.sendToAll(
                    notification.getTitle(),
                    notification.getMessage(),
                    data
            );

            log.info("FCM push notification sent for notification: {}", notification.getId());
        } catch (Exception e) {
            // Không throw exception để không ảnh hưởng flow chính
            log.error("Failed to send FCM push notification: {}", e.getMessage());
        }
    }

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
                .build();
    }
}
