package com.worthybitbuilders.squadsense.models;

import java.util.List;

public class ChatRoom {
    public static class Member {
        public String _id;
        public String name;
        public String profileImagePath;

        public Member(String _id, String name, String profileImagePath) {
            this._id = _id;
            this.name = name;
            this.profileImagePath = profileImagePath;
        }

        public String get_id() {
            return _id;
        }

        public String getName() {
            return name;
        }

        public String getProfileImagePath() {
            return profileImagePath;
        }
    }
    private String _id;
    private String title;
    private List<Member> members;
    private String logoPath;
    private String lastMessage;
    private String lastMessageTime;
    // if false it means 1vs1 chat room else it is a group chat
    private boolean isGroup;

    public ChatRoom() {}

    public ChatRoom(String _id, String title, List<Member> members, String logoPath, String lastMessageTime, boolean isGroup) {
        this._id = _id;
        this.title = title;
        this.members = members;
        this.logoPath = logoPath;
        this.lastMessageTime = lastMessageTime;
        this.isGroup = isGroup;
    }

    public String get_id() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public List<Member> getMembers() {
        return members;
    }

    public String getLogoPath() {
        return logoPath;
    }

    // TODO: LAST MESSAGE FUNCTIONALITY
    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastMessageTime() { return lastMessageTime; }

    public boolean isGroup() {
        return isGroup;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }
}
