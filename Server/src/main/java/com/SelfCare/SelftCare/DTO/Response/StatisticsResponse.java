package com.SelfCare.SelftCare.DTO.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticsResponse {
    long totalUsers;
    long activeUsers;
    long totalExercises;
    long totalFoods;
    long totalNotifications;
    long unreadNotifications;
}