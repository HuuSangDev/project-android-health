package com.SelfCare.SelftCare.DTO.Gemini;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeminiResponse {
    @JsonProperty("candidates")
    private List<Candidate> candidates;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Candidate {
        @JsonProperty("content")
        private Content content;
        
        @JsonProperty("finishReason")
        private String finishReason;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Content {
        @JsonProperty("parts")
        private List<Part> parts;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Part {
        @JsonProperty("text")
        private String text;
    }
}

