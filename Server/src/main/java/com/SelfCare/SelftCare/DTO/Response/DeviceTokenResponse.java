package com.SelfCare.SelftCare.DTO.Response;

import com.SelfCare.SelftCare.Enum.Platform;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceTokenResponse {
    Long id;
    String token;
    Platform platform;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
