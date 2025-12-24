package com.SelfCare.SelftCare.Controller;

import com.SelfCare.SelftCare.DTO.ApiResponse;
import com.SelfCare.SelftCare.DTO.Request.DeviceTokenRequest;
import com.SelfCare.SelftCare.DTO.Response.DeviceTokenResponse;
import com.SelfCare.SelftCare.Service.DeviceTokenService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/device-tokens")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeviceTokenController {

    DeviceTokenService deviceTokenService;

    /**
     * Đăng ký FCM device token
     * POST /app/device-tokens
     * Body: { "token": "...", "platform": "ANDROID" }
     */
    @PostMapping
    public ApiResponse<DeviceTokenResponse> registerToken(@Valid @RequestBody DeviceTokenRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<DeviceTokenResponse>builder()
                .result(deviceTokenService.registerToken(email, request))
                .message("Device token registered successfully")
                .build();
    }

    /**
     * Hủy đăng ký device token (khi logout)
     * DELETE /app/device-tokens/{token}
     */
    @DeleteMapping("/{token}")
    public ApiResponse<Void> unregisterToken(@PathVariable String token) {
        deviceTokenService.unregisterToken(token);
        return ApiResponse.<Void>builder()
                .message("Device token unregistered successfully")
                .build();
    }

    /**
     * Hủy tất cả token của user (logout từ tất cả thiết bị)
     * DELETE /app/device-tokens/all
     */
    @DeleteMapping("/all")
    public ApiResponse<Void> unregisterAllTokens() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        deviceTokenService.unregisterAllTokensForUser(email);
        return ApiResponse.<Void>builder()
                .message("All device tokens unregistered successfully")
                .build();
    }

    /**
     * Lấy danh sách token của user hiện tại
     * GET /app/device-tokens/my-tokens
     */
    @GetMapping("/my-tokens")
    public ApiResponse<List<DeviceTokenResponse>> getMyTokens() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<List<DeviceTokenResponse>>builder()
                .result(deviceTokenService.getMyTokens(email))
                .message("Get device tokens successfully")
                .build();
    }
}
