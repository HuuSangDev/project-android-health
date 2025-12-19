package com.SelfCare.SelftCare.DTO.Request;

import com.SelfCare.SelftCare.Enum.Gender;
import com.SelfCare.SelftCare.Enum.Goal;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserProfileRequest {
    LocalDate dateOfBirth;
    Gender gender;
    Double height;
    Double weight;
    Goal healthGoal;

}
