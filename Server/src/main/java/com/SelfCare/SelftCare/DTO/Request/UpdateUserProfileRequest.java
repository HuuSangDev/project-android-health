package com.SelfCare.SelftCare.DTO.Request;

import com.SelfCare.SelftCare.Enum.Gender;
import com.SelfCare.SelftCare.Enum.Goal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UpdateUserProfileRequest {

    @NotNull
    Gender gender;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    LocalDate dateOfBirth;
    @NotNull
    Double height;
    @NotNull
    Double weight;
    @NotBlank
    Goal healthGoal;
    MultipartFile avatar;


}
