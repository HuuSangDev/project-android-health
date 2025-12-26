package com.example.app_selfcare;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.Adapter.ChatAdapter;
import com.example.app_selfcare.Data.Model.Request.ChatRequest;
import com.example.app_selfcare.Data.Model.Response.ApiResponse;
import com.example.app_selfcare.Data.Model.Response.ChatResponse;
import com.example.app_selfcare.Data.remote.ApiClient;
import com.example.app_selfcare.Data.remote.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ChatActivity - Màn hình chat với AI trợ lý sức khỏe
 * 
 * Chức năng chính:
 * - Giao diện chat realtime với AI (Gemini)
 * - Gửi tin nhắn và nhận phản hồi từ AI
 * - Lưu trữ conversationId để duy trì ngữ cảnh hội thoại
 * - Hiển thị loading khi đang chờ phản hồi
 */
public class ChatActivity extends AppCompatActivity {

    // Views
    private RecyclerView recyclerViewMessages;
    private TextInputEditText editTextMessage;
    private FloatingActionButton buttonSend;
    private ProgressBar progressBar;
    
    // Adapter quản lý danh sách tin nhắn
    private ChatAdapter chatAdapter;
    
    // ID cuộc hội thoại để duy trì ngữ cảnh với AI
    private String conversationId = null;

    /**
     * Khởi tạo Activity
     * - Thiết lập toolbar với nút back
     * - Khởi tạo RecyclerView cho danh sách tin nhắn
     * - Thiết lập các sự kiện cho input và button
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Thiết lập Toolbar với nút back
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Ánh xạ các view
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        progressBar = findViewById(R.id.progressBar);

        // Thiết lập RecyclerView với LinearLayoutManager
        chatAdapter = new ChatAdapter();
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(chatAdapter);

        // Thêm tin nhắn chào mừng từ bot
        chatAdapter.addBotMessage("Xin chào! Tôi là trợ lý AI chuyên về chăm sóc sức khỏe. Tôi có thể giúp gì cho bạn hôm nay?");

        // Bật/tắt nút gửi dựa trên nội dung input
        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Chỉ bật nút gửi khi có nội dung
                buttonSend.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Sự kiện click nút gửi
        buttonSend.setOnClickListener(v -> sendMessage());

        // Gửi tin nhắn khi nhấn Enter (tùy chọn)
        editTextMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                sendMessage();
                return true;
            }
            return false;
        });

        // Tự động cuộn xuống khi có tin nhắn mới
        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerViewMessages.scrollToPosition(chatAdapter.getItemCount() - 1);
            }
        });
    }

    /**
     * Gửi tin nhắn đến AI và xử lý phản hồi
     * - Thêm tin nhắn user vào chat
     * - Gọi API chat với Gemini AI
     * - Hiển thị phản hồi từ AI
     */
    private void sendMessage() {
        String message = editTextMessage.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }

        // Add user message to chat
        chatAdapter.addMessage(message, true);
        editTextMessage.setText("");

        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        buttonSend.setEnabled(false);
        editTextMessage.setEnabled(false);

        // Create request
        ChatRequest request = new ChatRequest(message, conversationId);

        // Call API
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<ChatResponse>> call = apiService.chat(request);

        call.enqueue(new Callback<ApiResponse<ChatResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ChatResponse>> call, Response<ApiResponse<ChatResponse>> response) {
                progressBar.setVisibility(View.GONE);
                buttonSend.setEnabled(true);
                editTextMessage.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ChatResponse> apiResponse = response.body();

                    if (apiResponse.getCode() == 200 && apiResponse.getResult() != null) {
                        ChatResponse chatResponse = apiResponse.getResult();
                        
                        // Save conversation ID for context
                        conversationId = chatResponse.getConversationId();
                        
                        // Add bot response
                        chatAdapter.addBotMessage(chatResponse.getResponse());
                    } else {
                        String errorMsg = apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Không nhận được phản hồi từ server";
                        Toast.makeText(ChatActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "Lỗi kết nối đến server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ChatResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                buttonSend.setEnabled(true);
                editTextMessage.setEnabled(true);
                
                String errorMsg = "Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng.";
                if (t.getMessage() != null) {
                    errorMsg += "\n" + t.getMessage();
                }
                Toast.makeText(ChatActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Xử lý sự kiện click trên menu item
     * @param item Menu item được click
     * @return true nếu đã xử lý, false nếu không
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Xử lý nút back trên toolbar
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

