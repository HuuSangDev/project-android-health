package com.SelfCare.SelftCare.Entity;

import com.SelfCare.SelftCare.Enum.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String title;
    
    String message;

    @Enumerated(EnumType.STRING)
    NotificationType type; // FOOD hoặc EXERCISE

    Long targetId; // ID của Food hoặc Exercise được tạo

    @Builder.Default
    boolean isRead = false;

    LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
