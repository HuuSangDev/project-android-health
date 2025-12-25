package com.SelfCare.SelftCare.Service;

import com.SelfCare.SelftCare.DTO.Response.StatisticsResponse;
import com.SelfCare.SelftCare.Repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StatisticsService {

    UserRepository userRepository;
    ExerciseRepository exerciseRepository;
    FoodRepository foodRepository;
    NotificationRepository notificationRepository;

    /**
     * Lấy thống kê tổng quan cho admin dashboard
     */
    public StatisticsResponse getOverviewStatistics() {
        log.info("Getting overview statistics");

        // Đếm tổng số users
        long totalUsers = userRepository.count();

        // Đếm users hoạt động trong 30 ngày qua (có thể tùy chỉnh logic này)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long activeUsers = userRepository.countByCreatedAtAfter(thirtyDaysAgo);

        // Đếm tổng số bài tập
        long totalExercises = exerciseRepository.count();

        // Đếm tổng số món ăn
        long totalFoods = foodRepository.count();

        // Đếm tổng số thông báo
        long totalNotifications = notificationRepository.count();

        // Đếm thông báo chưa đọc
        long unreadNotifications = notificationRepository.countByIsReadFalse();

        StatisticsResponse response = StatisticsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalExercises(totalExercises)
                .totalFoods(totalFoods)
                .totalNotifications(totalNotifications)
                .unreadNotifications(unreadNotifications)
                .build();

        log.info("Statistics: Users={}, Active={}, Exercises={}, Foods={}, Notifications={}, Unread={}", 
                totalUsers, activeUsers, totalExercises, totalFoods, totalNotifications, unreadNotifications);

        return response;
    }
}