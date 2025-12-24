package com.SelfCare.SelftCare.Controller;

import com.SelfCare.SelftCare.DTO.ApiResponse;
import com.SelfCare.SelftCare.DTO.Request.CreateDailyLogRequest;
import com.SelfCare.SelftCare.DTO.Response.DailyLogResponse;
import com.SelfCare.SelftCare.Service.DailyLogService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/daily-logs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DailyLogController {

    DailyLogService dailyLogService;

    @GetMapping("/last-7-days")
    public ApiResponse<List<DailyLogResponse>> getLast7Days() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return ApiResponse.<List<DailyLogResponse>>builder()
                .result(dailyLogService.getCurrentWeekLogs(email))
                .message("Dữ liệu tuần hiện tại")
                .build();
    }

    @GetMapping("/previous-week")
    public ApiResponse<List<DailyLogResponse>> getPreviousWeek() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return ApiResponse.<List<DailyLogResponse>>builder()
                .result(dailyLogService.getPreviousWeekLogs(email))
                .message("Dữ liệu tuần trước")
                .build();
    }

    @GetMapping("/today")
    public ApiResponse<DailyLogResponse> getTodayLog() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return ApiResponse.<DailyLogResponse>builder()
                .result(dailyLogService.getTodayLog(email))
                .message("Dữ liệu hôm nay")
                .build();
    }

    @PostMapping("/create-or-update")
    public ApiResponse<DailyLogResponse> createOrUpdateTodayLog(@Valid @RequestBody CreateDailyLogRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return ApiResponse.<DailyLogResponse>builder()
                .result(dailyLogService.createOrUpdateTodayLog(email, request))
                .message("Cập nhật thông tin sức khỏe thành công")
                .build();
    }

    @PostMapping("/generate-sample-data")
    public ApiResponse<String> generateSampleData() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        dailyLogService.generateSampleDataIfNeeded(email);
        return ApiResponse.<String>builder()
                .result("Dữ liệu mẫu đã được tạo")
                .message("Thành công")
                .build();
    }
}