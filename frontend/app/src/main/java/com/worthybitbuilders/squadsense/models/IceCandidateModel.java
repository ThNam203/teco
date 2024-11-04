package com.worthybitbuilders.squadsense.models;

public class IceCandidateModel {
    String chatRoomId;
    String sdpMid;
    int sdpMLineIndex;
    String sdpCandidate;

    public IceCandidateModel() {}

    public IceCandidateModel(String chatRoomId, String sdpMid, int sdpMLineIndex, String sdpCandidate) {
        this.chatRoomId = chatRoomId;
        this.sdpMid = sdpMid;
        this.sdpMLineIndex = sdpMLineIndex;
        this.sdpCandidate = sdpCandidate;
    }

    public String getSdpMid() {
        return sdpMid;
    }

    public int getSdpMLineIndex() {
        return sdpMLineIndex;
    }

    public String getSdpCandidate() {
        return sdpCandidate;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }
}
