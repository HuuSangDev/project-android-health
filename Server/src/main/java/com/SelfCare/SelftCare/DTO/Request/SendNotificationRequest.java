package com.SelfCare.SelftCare.DTO.Request;

import com.SelfCare.SelftCare.Enum.NotificationType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendNotificationRequest {
    String title;
    String message;
    NotificationType type;  // FOOD hoặc EXERCISE
    Long targetId;          // ID của food/exercise (optional)
}
