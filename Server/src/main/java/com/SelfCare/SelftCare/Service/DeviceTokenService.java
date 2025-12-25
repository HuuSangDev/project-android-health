package com.SelfCare.SelftCare.Service;

import com.SelfCare.SelftCare.DTO.Request.DeviceTokenRequest;
import com.SelfCare.SelftCare.DTO.Response.DeviceTokenResponse;
import com.SelfCare.SelftCare.Entity.DeviceToken;
import com.SelfCare.SelftCare.Entity.User;
import com.SelfCare.SelftCare.Exception.AppException;
import com.SelfCare.SelftCare.Exception.ErrorCode;
import com.SelfCare.SelftCare.Repository.DeviceTokenRepository;
import com.SelfCare.SelftCare.Repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DeviceTokenService {

    DeviceTokenRepository deviceTokenRepository;
    UserRepository userRepository;

    /**
     * Đăng ký hoặc cập nhật device token
     * Logic upsert:
     * - Nếu token đã tồn tại → update user, platform, set isActive=true
     * - Nếu chưa tồn tại → insert mới
     */
    @Transactional
    public DeviceTokenResponse registerToken(String email, DeviceTokenRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Optional<DeviceToken> existingToken = deviceTokenRepository.findByToken(request.getToken());

        DeviceToken deviceToken;
        if (existingToken.isPresent()) {
            // Token đã tồn tại → update
            deviceToken = existingToken.get();
            deviceToken.setUser(user);
            deviceToken.setPlatform(request.getPlatform());
            deviceToken.setIsActive(true);
            log.info("Updated existing device token for user: {}", email);
        } else {
            // Token chưa tồn tại → insert mới
            deviceToken = DeviceToken.builder()
                    .user(user)
                    .token(request.getToken())
                    .platform(request.getPlatform())
                    .isActive(true)
                    .build();
            log.info("Registered new device token for user: {}", email);
        }

        DeviceToken saved = deviceTokenRepository.save(deviceToken);
        return toResponse(saved);
    }

    /**
     * Hủy đăng ký device token (khi user logout)
     */
    @Transactional
    public void unregisterToken(String token) {
        deviceTokenRepository.findByToken(token).ifPresent(deviceToken -> {
            deviceToken.setIsActive(false);
            deviceTokenRepository.save(deviceToken);
            log.info("Unregistered device token");
        });
    }

    /**
     * Hủy tất cả token của user (khi user logout từ tất cả thiết bị)
     */
    @Transactional
    public void unregisterAllTokensForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<DeviceToken> tokens = deviceTokenRepository.findAllByUserIdAndIsActiveTrue(user.getId());
        tokens.forEach(token -> token.setIsActive(false));
        deviceTokenRepository.saveAll(tokens);
        log.info("Unregistered all device tokens for user: {}", email);
    }

    /**
     * Lấy danh sách token của user hiện tại
     */
    public List<DeviceTokenResponse> getMyTokens(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return deviceTokenRepository.findAllByUserIdAndIsActiveTrue(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert Entity sang Response
     */
    private DeviceTokenResponse toResponse(DeviceToken deviceToken) {
        return DeviceTokenResponse.builder()
                .id(deviceToken.getId())
                .token(deviceToken.getToken())
                .platform(deviceToken.getPlatform())
                .isActive(deviceToken.getIsActive())
                .createdAt(deviceToken.getCreatedAt())
                .updatedAt(deviceToken.getUpdatedAt())
                .build();
    }
}
