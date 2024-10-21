package com.worthybitbuilders.squadsense.models.board_models;

public class BoardRowHeaderModel {
    private String title;

    // the "+ New row" row
    private transient Boolean isAddNewRowRow = false;

    public BoardRowHeaderModel(String title, Boolean isAddNewRowRow) {
        this.title = title;
        this.isAddNewRowRow = isAddNewRowRow;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getIsAddNewRowRow() {
        return isAddNewRowRow;
    }
}
