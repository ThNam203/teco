package com.worthybitbuilders.squadsense.models;

import java.util.UUID;

public class ChatMessageRequest {
    private String chatRoomId;
    private String message;
    private String senderId;

    public ChatMessageRequest(String chatRoomId, String message, String senderId) {
        this.chatRoomId = chatRoomId;
        this.message = message;
        this.senderId = senderId;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
