package com.SelfCare.SelftCare.DTO.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class FoodCategoryResponse {
    Long categoryId;
    String categoryName;
    String description;
    int foodCount;
}
