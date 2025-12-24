package com.SelfCare.SelftCare.Controller;

import com.SelfCare.SelftCare.DTO.ApiResponse;
import com.SelfCare.SelftCare.DTO.Request.SendNotificationRequest;
import com.SelfCare.SelftCare.DTO.Response.NotificationResponse;
import com.SelfCare.SelftCare.Service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {

    NotificationService notificationService;

    /**
     * API để Admin gửi thông báo tùy chỉnh
     * POST /notifications/send
     * Body: { "title": "...", "message": "...", "type": "FOOD/EXERCISE", "targetId": 123 }
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/send")
    public ApiResponse<Void> sendCustomNotification(@RequestBody SendNotificationRequest request) {
        notificationService.sendCustomNotification(
                request.getTitle(),
                request.getMessage(),
                request.getType(),
                request.getTargetId()
        );
        return ApiResponse.<Void>builder()
                .message("Đã gửi thông báo thành công")
                .build();
    }

    /**
     * API để Admin gửi thông báo broadcast (không cần targetId)
     * POST /notifications/broadcast
     * Body: { "title": "...", "message": "..." }
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/broadcast")
    public ApiResponse<Void> sendBroadcastNotification(@RequestBody SendNotificationRequest request) {
        notificationService.sendBroadcastNotification(
                request.getTitle(),
                request.getMessage()
        );
        return ApiResponse.<Void>builder()
                .message("Đã gửi thông báo broadcast thành công")
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<List<NotificationResponse>> getAllNotifications() {
        return ApiResponse.<List<NotificationResponse>>builder()
                .result(notificationService.getAllNotifications())
                .message("Danh sách thông báo")
                .build();
    }

    @GetMapping("/unread")
    public ApiResponse<List<NotificationResponse>> getUnreadNotifications() {
        return ApiResponse.<List<NotificationResponse>>builder()
                .result(notificationService.getUnreadNotifications())
                .message("Danh sách thông báo chưa đọc")
                .build();
    }

    @PutMapping("/{id}/read")
    public ApiResponse<NotificationResponse> markAsRead(@PathVariable Long id) {
        return ApiResponse.<NotificationResponse>builder()
                .result(notificationService.markAsRead(id))
                .message("Đã đánh dấu đã đọc")
                .build();
    }

    @PutMapping("/read-all")
    public ApiResponse<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ApiResponse.<Void>builder()
                .message("Đã đánh dấu tất cả đã đọc")
                .build();
    }
}
