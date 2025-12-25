package com.SelfCare.SelftCare.DTO.Response;

import com.SelfCare.SelftCare.Enum.Goal;
import com.SelfCare.SelftCare.Enum.NotificationType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    Long id;
    String title;
    String message;
    NotificationType type;
    Long targetId;
    Goal goal;
    boolean isRead;
    LocalDateTime createdAt;
}
