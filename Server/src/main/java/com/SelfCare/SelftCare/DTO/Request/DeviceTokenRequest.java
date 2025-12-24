package com.SelfCare.SelftCare.DTO.Request;

import com.SelfCare.SelftCare.Enum.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceTokenRequest {

    @NotBlank(message = "Token không được để trống")
    String token;

    @NotNull(message = "Platform không được để trống")
    Platform platform;
}
