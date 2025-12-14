# Hướng dẫn tích hợp Chatbot Gemini

## Tổng quan
Dự án đã được tích hợp với Google Gemini API để cung cấp tính năng chatbot cho ứng dụng Healthcare.

## Cấu trúc triển khai

### Backend (Spring Boot)

#### 1. Cấu hình API Key
File: `src/main/resources/application.yaml`

```yaml
gemini:
  api-key: "${GEMINI_API_KEY:YOUR_GEMINI_API_KEY_HERE}"
  api-url: "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent"
```

**Lưu ý quan trọng:**
- Thay `YOUR_GEMINI_API_KEY_HERE` bằng API Key thực tế của bạn
- Hoặc set biến môi trường `GEMINI_API_KEY` để bảo mật hơn
- **KHÔNG BAO GIỜ** commit API Key vào Git

#### 2. Endpoint API

**URL:** `POST /app/api/chat`

**Request Body:**
```json
{
  "message": "Câu hỏi của bạn về sức khỏe",
  "conversationId": "optional-conversation-id" 
}
```

**Response:**
```json
{
  "code": 200,
  "message": "Thành công",
  "result": {
    "response": "Câu trả lời từ Gemini AI",
    "conversationId": "conversation-id"
  }
}
```

#### 3. Các thành phần đã tạo

- **DTO:**
  - `ChatRequest.java` - Request từ client
  - `ChatResponse.java` - Response trả về client
  - `GeminiRequest.java` - Request gửi đến Gemini API
  - `GeminiResponse.java` - Response từ Gemini API

- **Service:**
  - `GeminiService.java` - Xử lý logic gọi Gemini API

- **Controller:**
  - `ChatController.java` - Endpoint `/api/chat`

- **Configuration:**
  - `RestTemplateConfig.java` - Cấu hình RestTemplate cho HTTP client

#### 4. Security

Endpoint `/api/chat/**` hiện đang được cấu hình là `permitAll()` trong `SecurityConfig.java`.

**Nếu muốn yêu cầu authentication:**
Sửa file `SecurityConfig.java`, xóa `/api/chat/**` khỏi danh sách `permitAll()`.

## Cách lấy Gemini API Key

1. Truy cập [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Đăng nhập bằng tài khoản Google
3. Tạo API Key mới
4. Copy API Key và thêm vào `application.yaml` hoặc biến môi trường

## Testing

### Sử dụng cURL:
```bash
curl -X POST http://localhost:8080/app/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Tôi nên ăn gì để giảm cân?"
  }'
```

### Sử dụng Postman:
1. Method: POST
2. URL: `http://localhost:8080/app/api/chat`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):
```json
{
  "message": "Tôi nên ăn gì để giảm cân?"
}
```

## Xử lý lỗi

Hệ thống đã được tích hợp xử lý lỗi với các mã lỗi:
- `GEMINI_API_ERROR (300)`: Lỗi khi gọi Gemini API
- `UNCATEGORIZED_EXCEPTION (500)`: Lỗi không xác định

Tất cả lỗi sẽ được trả về dưới dạng `ApiResponse` với format chuẩn.

## Tính năng đặc biệt

1. **Healthcare Context**: Prompt tự động được thêm context về healthcare để Gemini trả lời phù hợp hơn
2. **Conversation ID**: Hỗ trợ duy trì context cuộc hội thoại (có thể mở rộng thêm trong tương lai)
3. **Safety Filter**: Tự động kiểm tra và xử lý khi câu hỏi bị chặn bởi bộ lọc an toàn của Gemini

## Lưu ý quan trọng

⚠️ **Cảnh báo y tế**: Chatbot chỉ cung cấp thông tin tham khảo, không thay thế cho tư vấn y tế chuyên nghiệp. Người dùng được cảnh báo trong prompt.

## Bước tiếp theo (Frontend Android)

1. Tạo Retrofit/OkHttp client để gọi API
2. Tạo UI cho chat interface
3. Xử lý hiển thị response và error handling
4. (Optional) Implement conversation history với conversationId

