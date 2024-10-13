package com.worthybitbuilders.squadsense.models;

import java.util.UUID;

public class ChatMessage {
    private String _id;
    private String chatRoomId;
    private String message;
    private String senderId;
    private String createdAt;

    public ChatMessage(String _id, String chatRoomId, String message, String senderId, String createdAt) {
        this._id = _id;
        this.chatRoomId = chatRoomId;
        this.message = message;
        this.senderId = senderId;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "_id='" + _id + '\'' +
                ", chatRoomId='" + chatRoomId + '\'' +
                ", message='" + message + '\'' +
                ", senderId='" + senderId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }

    public ChatMessage(String chatRoomId, String message, String senderId) {
        this._id = UUID.randomUUID().toString();
        this.chatRoomId = chatRoomId;
        this.message = message;
        this.senderId = senderId;
        this.createdAt = "random created at";
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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
