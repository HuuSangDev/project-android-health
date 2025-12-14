package com.example.app_selfcare.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_selfcare.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_USER = 1;
    private static final int TYPE_BOT = 2;

    private List<ChatMessage> messages = new ArrayList<>();

    public void addMessage(String message, boolean isUser) {
        messages.add(new ChatMessage(message, isUser));
        notifyItemInserted(messages.size() - 1);
    }

    public void addBotMessage(String message) {
        messages.add(new ChatMessage(message, false));
        notifyItemInserted(messages.size() - 1);
    }

    public void clearMessages() {
        messages.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser ? TYPE_USER : TYPE_BOT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_user, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_bot, parent, false);
            return new BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message.message);
        } else if (holder instanceof BotMessageViewHolder) {
            ((BotMessageViewHolder) holder).bind(message.message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;

        UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
        }

        void bind(String message) {
            textViewMessage.setText(message);
        }
    }

    static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;

        BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
        }

        void bind(String message) {
            textViewMessage.setText(message);
        }
    }

    static class ChatMessage {
        String message;
        boolean isUser;

        ChatMessage(String message, boolean isUser) {
            this.message = message;
            this.isUser = isUser;
        }
    }
}

