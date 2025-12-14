package com.SelfCare.SelftCare.Controller;

import com.SelfCare.SelftCare.DTO.ApiResponse;
import com.SelfCare.SelftCare.DTO.Request.ChatRequest;
import com.SelfCare.SelftCare.DTO.Response.ChatResponse;
import com.SelfCare.SelftCare.Service.GeminiService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ChatController {

    GeminiService geminiService;

    /**
     * Endpoint để gửi câu hỏi đến chatbot Gemini
     * 
     * @param request ChatRequest chứa message và conversationId (optional)
     * @return ApiResponse chứa ChatResponse với câu trả lời từ Gemini
     */
    @PostMapping
    public ApiResponse<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        ChatResponse response = geminiService.chat(
            request.getMessage(), 
            request.getConversationId()
        );
        
        return ApiResponse.<ChatResponse>builder()
                .result(response)
                .message("Thành công")
                .code(200)
                .build();
    }
}

