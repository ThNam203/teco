package com.worthybitbuilders.squadsense.models;

public class SdpOfferModel {
    private String chatRoomId;
    private String sdp;

    public SdpOfferModel() {
    }

    public SdpOfferModel(String chatRoomId, String sdp) {
        this.chatRoomId = chatRoomId;
        this.sdp = sdp;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public String getSdp() {
        return sdp;
    }
}
