package com.worthybitbuilders.squadsense.models;

public class SdpOfferModel {
    private String chatRoomId;
    private String sdp;
    private String callerId;
    private String callerName;
    private String callerImagePath;

    public SdpOfferModel(String chatRoomId, String sdp, String callerId, String callerName, String callerImagePath) {
        this.chatRoomId = chatRoomId;
        this.sdp = sdp;
        this.callerId = callerId;
        this.callerName = callerName;
        this.callerImagePath = callerImagePath;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public String getSdp() {
        return sdp;
    }

    public String getCallerId() {
        return callerId;
    }

    public String getCallerName() {
        return callerName;
    }

    public String getCallerImagePath() {
        return callerImagePath;
    }
}
