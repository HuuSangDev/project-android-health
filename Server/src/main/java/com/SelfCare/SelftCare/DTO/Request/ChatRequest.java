package com.SelfCare.SelftCare.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatRequest {
    @NotBlank(message = "Câu hỏi không được để trống")
    String message;
    
    // Optional: có thể thêm conversationId để duy trì context
    String conversationId;
}

