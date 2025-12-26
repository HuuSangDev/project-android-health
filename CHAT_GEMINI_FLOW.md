# LUỒNG HOẠT ĐỘNG CHỨC NĂNG CHAT VỚI GEMINI AI

## Tổng quan
Chức năng chat cho phép người dùng trò chuyện với AI trợ lý sức khỏe (Gemini) để nhận tư vấn về chăm sóc sức khỏe, dinh dưỡng, tập luyện.

---

## KIẾN TRÚC TỔNG QUAN

```
┌─────────────────┐      HTTP/REST      ┌──────────────────┐      HTTP/REST      ┌─────────────────┐
│  Android Client │ ──────────────────> │  Spring Backend  │ ──────────────────> │   Gemini API    │
│   (Frontend)    │ <────────────────── │    (Server)      │ <────────────────── │   (Google AI)   │
└─────────────────┘                     └──────────────────┘                     └─────────────────┘
```

---

## LUỒNG CHI TIẾT (STEP BY STEP)

### 🔵 FRONTEND - ANDROID CLIENT

#### **Bước 1: User Interface (ChatActivity.java)**
📍 **File**: `Androi_Client/app/src/main/java/com/example/app_selfcare/ChatActivity.java`

**Chức năng**:
- Hiển thị giao diện chat với RecyclerView
- Nhận input từ người dùng qua EditText
- Hiển thị tin nhắn của user và bot

**Các thành phần chính**:
```java
- RecyclerView recyclerViewMessages    // Danh sách tin nhắn
- TextInputEditText editTextMessage     // Ô nhập tin nhắn
- FloatingActionButton buttonSend       // Nút gửi
- ProgressBar progressBar               // Loading indicator
- ChatAdapter chatAdapter               // Adapter quản lý tin nhắn
- String conversationId                 // ID cuộc hội thoại (duy trì context)
```

**Khi user nhấn nút gửi**:
```java
private void sendMessage() {
    // 1. Lấy tin nhắn từ EditText
    String message = editTextMessage.getText().toString().trim();
    
    // 2. Thêm tin nhắn user vào RecyclerView
    chatAdapter.addMessage(message, true);
    
    // 3. Hiển thị loading
    progressBar.setVisibility(View.VISIBLE);
    buttonSend.setEnabled(false);
    
    // 4. Tạo request object
    ChatRequest request = new ChatRequest(message, conversationId);
    
    // 5. Gọi API
    ApiService apiService = ApiClient.getClient().create(ApiService.class);
    Call<ApiResponse<ChatResponse>> call = apiService.chat(request);
    call.enqueue(callback);
}
```

---

#### **Bước 2: Data Models**

**📄 ChatRequest.java** - Request gửi lên server
```java
public class ChatRequest {
    private String message;           // Tin nhắn từ user
    private String conversationId;    // ID cuộc hội thoại (optional)
}
```

**📄 ChatResponse.java** - Response nhận từ server
```java
public class ChatResponse {
    private String response;          // Câu trả lời từ AI
    private String conversationId;    // ID cuộc hội thoại (để duy trì context)
}
```

---

#### **Bước 3: API Service Interface**
📍 **File**: `Androi_Client/app/src/main/java/com/example/app_selfcare/Data/remote/ApiService.java`

```java
@POST("app/api/chat")
Call<ApiResponse<ChatResponse>> chat(@Body ChatRequest request);
```

**Endpoint**: `POST /app/api/chat`

---

#### **Bước 4: Retrofit Client**
📍 **File**: `Androi_Client/app/src/main/