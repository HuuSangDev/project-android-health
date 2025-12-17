package com.SelfCare.SelftCare.DTO.Gemini;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeminiErrorResponse {
    @JsonProperty("error")
    private Error error;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Error {
        @JsonProperty("code")
        private Integer code;
        
        @JsonProperty("message")
        private String message;
        
        @JsonProperty("status")
        private String status;
    }
}

