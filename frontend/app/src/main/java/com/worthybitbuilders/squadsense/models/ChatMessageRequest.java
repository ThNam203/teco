package com.worthybitbuilders.squadsense.models;

import java.util.List;
import java.util.UUID;

public class ChatMessageRequest {
    private String chatRoomId;
    private String message;
    private String senderId;
    private List<ChatMessage.MessageFile> files;

    public ChatMessageRequest(String chatRoomId, String message, String senderId, List<ChatMessage.MessageFile> files) {
        this.chatRoomId = chatRoomId;
        this.message = message;
        this.senderId = senderId;
        this.files = files;
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

    public List<ChatMessage.MessageFile> getFiles() {
        return files;
    }

    public void setFiles(List<ChatMessage.MessageFile> files) {
        this.files = files;
    }
}
