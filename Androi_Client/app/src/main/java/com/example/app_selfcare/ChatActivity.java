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

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private TextInputEditText editTextMessage;
    private FloatingActionButton buttonSend;
    private ProgressBar progressBar;
    private ChatAdapter chatAdapter;
    private String conversationId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Initialize views
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        progressBar = findViewById(R.id.progressBar);

        // Setup RecyclerView
        chatAdapter = new ChatAdapter();
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(chatAdapter);

        // Add welcome message
        chatAdapter.addBotMessage("Xin chào! Tôi là trợ lý AI chuyên về chăm sóc sức khỏe. Tôi có thể giúp gì cho bạn hôm nay?");

        // Enable/disable send button based on input
        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonSend.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Send button click
        buttonSend.setOnClickListener(v -> sendMessage());

        // Send on Enter key (optional)
        editTextMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                sendMessage();
                return true;
            }
            return false;
        });

        // Scroll to bottom when new message is added
        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerViewMessages.scrollToPosition(chatAdapter.getItemCount() - 1);
            }
        });
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

