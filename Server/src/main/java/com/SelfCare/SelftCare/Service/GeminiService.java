package com.SelfCare.SelftCare.Service;

import com.SelfCare.SelftCare.DTO.Gemini.GeminiRequest;
import com.SelfCare.SelftCare.DTO.Gemini.GeminiResponse;
import com.SelfCare.SelftCare.DTO.Response.ChatResponse;
import com.SelfCare.SelftCare.Exception.AppException;
import com.SelfCare.SelftCare.Exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GeminiService {

    RestTemplate restTemplate;
    ObjectMapper objectMapper;

    @NonFinal
    @Value("${gemini.api-key}")
    String apiKey;

    @NonFinal
    @Value("${gemini.api-url}")
    String apiUrl;
    
    public GeminiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Gửi câu hỏi đến Gemini API và nhận câu trả lời
     *
     * @param message Câu hỏi từ người dùng
     * @param conversationId ID cuộc hội thoại (optional, để duy trì context)
     * @return ChatResponse chứa câu trả lời từ Gemini
     */
    public ChatResponse chat(String message, String conversationId) {
        try {
            // Tạo request body cho Gemini API
            GeminiRequest request = buildGeminiRequest(message);

            // Tạo URL với API key (sử dụng UriComponentsBuilder để encode đúng)
            URI url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .queryParam("key", apiKey)
                    .build()
                    .toUri();

            // Tạo headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Tạo HTTP entity
            HttpEntity<GeminiRequest> entity = new HttpEntity<>(request, headers);

            // Log request body dạng JSON để debug
            try {
                String requestJson = objectMapper.writeValueAsString(request);
                log.info("Gửi request đến Gemini API - URL: {}, Message: {}", url, message);
                log.debug("Request body JSON: {}", requestJson);
            } catch (Exception e) {
                log.warn("Không thể serialize request body: {}", e.getMessage());
            }

            // Gọi API
            ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    GeminiResponse.class
            );

            log.info("Nhận được response từ Gemini API - Status: {}", response.getStatusCode());
            log.debug("Response body: {}", response.getBody());

            // Xử lý response
            if (response.getBody() == null) {
                log.error("Response body từ Gemini API là null");
                throw new AppException(ErrorCode.GEMINI_API_ERROR, 
                    "Không nhận được phản hồi từ Gemini API");
            }

            if (response.getBody().getCandidates() == null || 
                response.getBody().getCandidates().isEmpty()) {
                log.error("Response không chứa candidates - Response: {}", response.getBody());
                throw new AppException(ErrorCode.GEMINI_API_ERROR, 
                    "Không nhận được phản hồi từ Gemini API");
            }

            GeminiResponse.Candidate candidate = response.getBody().getCandidates().get(0);
            
            // Kiểm tra finishReason
            if ("SAFETY".equals(candidate.getFinishReason())) {
                throw new AppException(ErrorCode.GEMINI_API_ERROR, 
                    "Câu hỏi của bạn đã bị chặn bởi bộ lọc an toàn của Gemini. Vui lòng thử lại với câu hỏi khác.");
            }

            String responseText = extractResponseText(candidate);

            // Tạo conversationId nếu chưa có
            if (conversationId == null || conversationId.isEmpty()) {
                conversationId = UUID.randomUUID().toString();
            }

            log.info("Nhận được phản hồi từ Gemini API thành công");

            return ChatResponse.builder()
                    .response(responseText)
                    .conversationId(conversationId)
                    .build();

        } catch (HttpClientErrorException e) {
            // Lỗi 4xx (Bad Request, Unauthorized, etc.)
            log.error("Lỗi HTTP Client khi gọi Gemini API - Status: {}, Body: {}", 
                e.getStatusCode(), e.getResponseBodyAsString(), e);
            String errorMessage = "Lỗi khi gọi Gemini API";
            if (e.getStatusCode().value() == 400) {
                errorMessage = "Yêu cầu không hợp lệ. Vui lòng kiểm tra lại API Key và format request.";
            } else if (e.getStatusCode().value() == 401 || e.getStatusCode().value() == 403) {
                errorMessage = "API Key không hợp lệ hoặc không có quyền truy cập.";
            }
            throw new AppException(ErrorCode.GEMINI_API_ERROR, errorMessage);
            
        } catch (HttpServerErrorException e) {
            // Lỗi 5xx (Internal Server Error, etc.)
            log.error("Lỗi HTTP Server khi gọi Gemini API - Status: {}, Body: {}", 
                e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new AppException(ErrorCode.GEMINI_API_ERROR, 
                "Lỗi từ phía Gemini API server. Vui lòng thử lại sau.");
            
        } catch (ResourceAccessException e) {
            // Lỗi kết nối (timeout, network, etc.)
            log.error("Lỗi kết nối đến Gemini API: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.GEMINI_API_ERROR, 
                "Không thể kết nối đến Gemini API. Vui lòng kiểm tra kết nối mạng và thử lại sau.");
            
        } catch (RestClientException e) {
            // Các lỗi RestTemplate khác
            log.error("Lỗi RestClient khi gọi Gemini API: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.GEMINI_API_ERROR, 
                "Lỗi khi gọi Gemini API: " + e.getMessage());
            
        } catch (AppException e) {
            throw e;
            
        } catch (Exception e) {
            log.error("Lỗi không xác định khi xử lý Gemini API: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.GEMINI_API_ERROR, 
                "Đã xảy ra lỗi khi xử lý yêu cầu. Vui lòng thử lại sau.");
        }
    }

    /**
     * Xây dựng request body cho Gemini API
     */
    private GeminiRequest buildGeminiRequest(String message) {
        // Tạo prompt với context cho healthcare
        String healthcarePrompt = buildHealthcarePrompt(message);

        GeminiRequest.Part part = GeminiRequest.Part.builder()
                .text(healthcarePrompt)
                .build();

        List<GeminiRequest.Part> parts = new ArrayList<>();
        parts.add(part);

        GeminiRequest.Content content = GeminiRequest.Content.builder()
                .parts(parts)
                .build();

        List<GeminiRequest.Content> contents = new ArrayList<>();
        contents.add(content);

        return GeminiRequest.builder()
                .contents(contents)
                .build();
    }

    /**
     * Xây dựng prompt với context cho healthcare
     */
    private String buildHealthcarePrompt(String userMessage) {
        return "Bạn là một trợ lý AI chuyên về chăm sóc sức khỏe và y tế. " +
               "Hãy trả lời câu hỏi của người dùng một cách chính xác, hữu ích và an toàn. " +
               "Lưu ý: Đây chỉ là thông tin tham khảo, không thay thế cho tư vấn y tế chuyên nghiệp. " +
               "Nếu có vấn đề sức khỏe nghiêm trọng, người dùng nên tham khảo ý kiến bác sĩ.\n\n" +
               "Câu hỏi: " + userMessage;
    }

    /**
     * Trích xuất text từ response của Gemini
     */
    private String extractResponseText(GeminiResponse.Candidate candidate) {
        if (candidate.getContent() == null || 
            candidate.getContent().getParts() == null || 
            candidate.getContent().getParts().isEmpty()) {
            throw new AppException(ErrorCode.GEMINI_API_ERROR, 
                "Không thể trích xuất câu trả lời từ phản hồi của Gemini");
        }

        return candidate.getContent().getParts().get(0).getText();
    }
}

