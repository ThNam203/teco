package com.worthybitbuilders.squadsense.models;

import java.util.UUID;

public class ChatMessage {
    public class MessageSender {
        public String _id;
        public String name;
        public String profileImagePath;

        public MessageSender(String _id, String name, String profileImagePath) {
            this._id = _id;
            this.name = name;
            this.profileImagePath = profileImagePath;
        }
    }
    private String _id;
    private String chatRoomId;
    private String message;

    private MessageSender sender;
    private String createdAt;

    public ChatMessage(String chatRoomId, String message, MessageSender sender) {
        this.chatRoomId = chatRoomId;
        this.message = message;
        this.sender = sender;
    }

    public ChatMessage(String _id, String chatRoomId, String message, MessageSender sender, String createdAt) {
        this._id = _id;
        this.chatRoomId = chatRoomId;
        this.message = message;
        this.sender = sender;
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String get_id() {
        return _id;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public String getMessage() {
        return message;
    }

    public MessageSender getSender() {
        return sender;
    }
}
