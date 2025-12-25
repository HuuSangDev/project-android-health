package com.SelfCare.SelftCare.Controller;

import com.SelfCare.SelftCare.DTO.ApiResponse;
import com.SelfCare.SelftCare.DTO.Response.StatisticsResponse;
import com.SelfCare.SelftCare.Service.StatisticsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsController {

    StatisticsService statisticsService;

    /**
     * API lấy thống kê tổng quan cho admin dashboard
     * GET /admin/statistics/overview
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/overview")
    public ApiResponse<StatisticsResponse> getOverviewStatistics() {
        return ApiResponse.<StatisticsResponse>builder()
                .result(statisticsService.getOverviewStatistics())
                .message("Thống kê tổng quan")
                .build();
    }
}