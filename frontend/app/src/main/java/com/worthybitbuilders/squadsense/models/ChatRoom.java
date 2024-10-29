package com.worthybitbuilders.squadsense.models;

public class ChatRoom {
    String title;
    UserModel[] members;
    String logoPath;

    public ChatRoom() {}
    public ChatRoom(String title, UserModel[] members, String logoPath)
    {
        this.title = title;
        this.members = members;
        this.logoPath = logoPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UserModel[] getMembers() {
        return members;
    }

    public void setMembers(UserModel[] members) {
        this.members = members;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
}
