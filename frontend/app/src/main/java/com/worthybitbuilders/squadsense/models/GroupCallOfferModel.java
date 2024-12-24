package com.worthybitbuilders.squadsense.models;

public class GroupCallOfferModel {
    private final String chatRoomId;

    public GroupCallOfferModel(String chatRoomId, String callerId) {
        this.chatRoomId = chatRoomId;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }
}
