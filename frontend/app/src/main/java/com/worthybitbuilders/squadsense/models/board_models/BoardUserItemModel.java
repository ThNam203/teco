package com.worthybitbuilders.squadsense.models.board_models;

import com.worthybitbuilders.squadsense.models.UserModel;

import java.util.ArrayList;
import java.util.List;

public class BoardUserItemModel extends BoardBaseItemModel {
    private List<UserModel> users;

    public BoardUserItemModel() {
        super("", "CellUser");
        this.users = new ArrayList<>();
    }

    public BoardUserItemModel(List<UserModel> users) {
        super("", "CellUser");
        this.users = users;
    }

    public List<UserModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserModel> users) {
        this.users = users;
    }
}
