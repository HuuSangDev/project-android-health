package com.SelfCare.SelftCare.DTO.Request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateDailyLogRequest {

    @NotNull(message = "Cân nặng không được để trống")
    Double currentWeight;

    @NotNull(message = "Chiều cao không được để trống")
    Double height;

    String notes;
}