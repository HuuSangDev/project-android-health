package com.SelfCare.SelftCare.Repository;

import com.SelfCare.SelftCare.Entity.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {

    // Lấy 7 ngày gần nhất của user
    @Query("SELECT dl FROM DailyLog dl WHERE dl.user.id = :userId AND dl.logDate >= :startDate ORDER BY dl.logDate ASC")
    List<DailyLog> findLast7Days(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);

    // Lấy 30 ngày gần nhất của user
    @Query("SELECT dl FROM DailyLog dl WHERE dl.user.id = :userId AND dl.logDate >= :startDate ORDER BY dl.logDate ASC")
    List<DailyLog> findLast30Days(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);

    // Lấy log của ngày cụ thể
    Optional<DailyLog> findByUserIdAndLogDate(Long userId, LocalDate logDate);

    // Lấy tất cả log của user
    @Query("SELECT dl FROM DailyLog dl WHERE dl.user.id = :userId ORDER BY dl.logDate DESC")
    List<DailyLog> findByUserId(@Param("userId") Long userId);
}