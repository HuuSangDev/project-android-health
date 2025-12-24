package com.SelfCare.SelftCare.Service;

import com.SelfCare.SelftCare.DTO.Request.CreateDailyLogRequest;
import com.SelfCare.SelftCare.DTO.Response.DailyLogResponse;
import com.SelfCare.SelftCare.Entity.DailyLog;
import com.SelfCare.SelftCare.Entity.User;
import com.SelfCare.SelftCare.Exception.AppException;
import com.SelfCare.SelftCare.Exception.ErrorCode;
import com.SelfCare.SelftCare.Repository.DailyLogRepository;
import com.SelfCare.SelftCare.Repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DailyLogService {

    DailyLogRepository dailyLogRepository;
    UserRepository userRepository;

    // Lấy dữ liệu tuần (thứ 2 - chủ nhật)
    public List<DailyLogResponse> getWeekLogs(String email, LocalDate weekStartMonday) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<DailyLog> logs = dailyLogRepository.findLast7Days(user.getId(), weekStartMonday);

        return logs.stream()
                .map(this::mapToDailyLogResponse)
                .collect(Collectors.toList());
    }

    // Lấy thứ 2 của tuần hiện tại
    private LocalDate getCurrentWeekMonday() {
        LocalDate today = LocalDate.now();
        int dayOfWeek = today.getDayOfWeek().getValue();
        return today.minusDays(dayOfWeek - 1);
    }

    // Lấy thứ 2 của tuần trước
    private LocalDate getPreviousWeekMonday() {
        return getCurrentWeekMonday().minusDays(7);
    }

    // Lấy dữ liệu tuần hiện tại
    public List<DailyLogResponse> getCurrentWeekLogs(String email) {
        return getWeekLogs(email, getCurrentWeekMonday());
    }

    // Lấy dữ liệu tuần trước
    public List<DailyLogResponse> getPreviousWeekLogs(String email) {
        return getWeekLogs(email, getPreviousWeekMonday());
    }

    // Lấy log của ngày cụ thể
    public DailyLogResponse getTodayLog(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return dailyLogRepository.findByUserIdAndLogDate(user.getId(), LocalDate.now())
                .map(this::mapToDailyLogResponse)
                .orElse(null);
    }

    // Tạo hoặc cập nhật log hôm nay
    @Transactional
    public DailyLogResponse createOrUpdateTodayLog(String email, CreateDailyLogRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        LocalDate today = LocalDate.now();

        // Kiểm tra log hôm nay đã tồn tại chưa
        DailyLog dailyLog = dailyLogRepository.findByUserIdAndLogDate(user.getId(), today)
                .orElse(null);

        if (dailyLog == null) {
            // Tạo mới
            dailyLog = DailyLog.builder()
                    .user(user)
                    .logDate(today)
                    .currentWeight(request.getCurrentWeight())
                    .notes(request.getNotes())
                    .build();
            log.info("Creating new daily log for user {} on {}", email, today);
        } else {
            // Cập nhật
            dailyLog.setCurrentWeight(request.getCurrentWeight());
            dailyLog.setNotes(request.getNotes());
            log.info("Updating daily log for user {} on {}", email, today);
        }

        DailyLog saved = dailyLogRepository.save(dailyLog);
        return mapToDailyLogResponse(saved);
    }

    // Tạo dữ liệu mẫu cho user (chỉ tuần trước)
    @Transactional
    public void generateSampleDataIfNeeded(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Tính thứ 2 của tuần trước
        LocalDate today = LocalDate.now();
        int dayOfWeek = today.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
        LocalDate currentWeekMonday = today.minusDays(dayOfWeek - 1);
        LocalDate previousWeekMonday = currentWeekMonday.minusDays(7);

        // Kiểm tra xem tuần trước đã có dữ liệu chưa
        List<DailyLog> existingLogs = dailyLogRepository.findLast7Days(user.getId(), previousWeekMonday);
        if (!existingLogs.isEmpty()) {
            return; // Đã có dữ liệu rồi
        }

        // Tạo dữ liệu mẫu cho tuần trước (thứ 2 - chủ nhật)
        double[] weights = {69.5, 69.2, 68.8, 68.5, 68.3, 68.0, 67.8};
        String[] notes = {"Tốt", "Bình thường", "Tốt", "Rất tốt", "Bình thường", "Tốt", "Rất tốt"};

        for (int i = 0; i < 7; i++) {
            LocalDate date = previousWeekMonday.plusDays(i);
            
            DailyLog dailyLog = DailyLog.builder()
                    .user(user)
                    .logDate(date)
                    .currentWeight(weights[i])
                    .notes(notes[i])
                    .build();
            
            dailyLogRepository.save(dailyLog);
        }

        log.info("Generated sample data for user {} for week starting {}", email, previousWeekMonday);
    }

    private DailyLogResponse mapToDailyLogResponse(DailyLog log) {
        return DailyLogResponse.builder()
                .logId(log.getLogId())
                .logDate(log.getLogDate())
                .currentWeight(log.getCurrentWeight())
                .notes(log.getNotes())
                .build();
    }
}