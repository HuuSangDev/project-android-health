package com.SelfCare.SelftCare.Entity;

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
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long achievementId;

    String achievementName; // tên thành tích
    String description; // mô tả thành tích
    String achievementType; // WEIGHT_LOSS, WEIGHT_GAIN, WORKOUT_STREAK, CALORIE_TRACKING, WATER_INTAKE, DIARY_STREAK
    Double targetValue; // giá trị cần đạt để hoàn thành
    String unit; // đơn vị (kg, ngày, lít...)
    String iconUrl; // URL icon thành tích
    String badgeColor; // màu badge
    @Builder.Default
    Integer points = 0; // điểm thưởng

    LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
