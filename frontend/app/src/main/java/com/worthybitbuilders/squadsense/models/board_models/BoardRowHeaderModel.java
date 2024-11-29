package com.worthybitbuilders.squadsense.models.board_models;

public class BoardRowHeaderModel {
    private String title;
    private boolean isDone;

    // the "+ New row" row
    private transient Boolean isAddNewRowRow;
    public BoardRowHeaderModel(String title) {
        this.title = title;
        this.isDone = false;
        this.isAddNewRowRow = false;
    }

    public BoardRowHeaderModel(String title, Boolean isAddNewRowRow) {
        this.title = title;
        this.isDone = false;
        this.isAddNewRowRow = isAddNewRowRow;
    }

    public BoardRowHeaderModel(String title, boolean isDone, Boolean isAddNewRowRow) {
        this.title = title;
        this.isDone = isDone;
        this.isAddNewRowRow = isAddNewRowRow;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public Boolean getIsAddNewRowRow() {
        return isAddNewRowRow;
    }
}
