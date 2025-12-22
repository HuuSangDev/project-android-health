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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationService {

    NotificationRepository notificationRepository;
    SimpMessagingTemplate messagingTemplate;

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
     * Lưu notification vào DB và gửi qua WebSocket
     */
    private void saveAndBroadcast(Notification notification) {
        // Lưu vào database
        Notification saved = notificationRepository.save(notification);
        log.info("Saved notification: {}", saved.getId());

        // Convert sang Response
        NotificationResponse response = toResponse(saved);

        // Gửi qua WebSocket tới topic /topic/notifications
        messagingTemplate.convertAndSend("/topic/notifications", response);
        log.info("Broadcasted notification to /topic/notifications");
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
