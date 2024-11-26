package com.worthybitbuilders.squadsense.models;

import java.util.List;
import java.util.UUID;

public class ChatMessage {
    public static class MessageSender {
        public String _id;
        public String name;
        public String profileImagePath;

        public MessageSender(String _id, String name, String profileImagePath) {
            this._id = _id;
            this.name = name;
            this.profileImagePath = profileImagePath;
        }
    }

    public static class MessageFile {
        public String location;
        public String name;
        public String fileType;

        public MessageFile(String location, String name, String fileType) {
            this.location = location;
            this.name = name;
            this.fileType = fileType;
        }
    }

    private String _id;
    private final String chatRoomId;
    private final String message;
    private final List<MessageFile> files;

    private final MessageSender sender;
    private String createdAt;

    public ChatMessage(String chatRoomId, String message, MessageSender sender, List<MessageFile> files) {
        this.chatRoomId = chatRoomId;
        this.message = message;
        this.sender = sender;
        this.files = files;
    }

    public ChatMessage(String _id, String chatRoomId, String message, MessageSender sender, String createdAt, List<MessageFile> files) {
        this._id = _id;
        this.chatRoomId = chatRoomId;
        this.message = message;
        this.sender = sender;
        this.createdAt = createdAt;
        this.files = files;
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

    public List<MessageFile> getFiles() {
        return files;
    }
}
