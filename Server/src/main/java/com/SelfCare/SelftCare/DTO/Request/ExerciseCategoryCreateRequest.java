package com.SelfCare.SelftCare.DTO.Request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseCategoryCreateRequest {
     String categoryName;
     String description;
     String iconUrl;
}
