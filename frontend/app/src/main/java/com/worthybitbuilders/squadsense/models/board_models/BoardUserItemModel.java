package com.worthybitbuilders.squadsense.models.board_models;

public class BoardUserItemModel extends BoardBaseItemModel {
    private String userId;
    private String userImagePath;

    public BoardUserItemModel() {
        super("", "CellUser");
        this.userId = "";
        this.userImagePath = "";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImagePath() {
        return userImagePath;
    }

    public void setUserImagePath(String userImagePath) {
        this.userImagePath = userImagePath;
    }
}
