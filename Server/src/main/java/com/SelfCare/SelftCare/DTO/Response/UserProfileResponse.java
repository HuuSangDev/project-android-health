package com.SelfCare.SelftCare.DTO.Response;

import com.SelfCare.SelftCare.Enum.Gender;
import com.SelfCare.SelftCare.Enum.Goal;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserProfileResponse {

     String avatarUrl;
     LocalDate dateOfBirth;
     Gender gender;
     Double height;
     Double weight;
     Goal healthGoal;
}
