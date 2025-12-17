package com.example.app_selfcare.Data.Model.Request;

public class ChatRequest {
    private String message;
    private String conversationId;

    public ChatRequest(String message, String conversationId) {
        this.message = message;
        this.conversationId = conversationId;
    }

    public ChatRequest(String message) {
        this.message = message;
        this.conversationId = null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}

