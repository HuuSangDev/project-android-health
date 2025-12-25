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
public class UserAchievement {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long userAchievementId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "achievement_id", nullable = false)
    Achievement achievement;

    Double achievedValue; // giá trị mà user đạt được
    LocalDateTime achievedAt; // thời gian đạt được
    @Builder.Default
    Boolean isNotified = false; // đã thông báo?

    @PrePersist
    protected void onCreate() {
        achievedAt = LocalDateTime.now();
    }
}
