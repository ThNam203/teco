package com.worthybitbuilders.squadsense.models.board_models;

public abstract class BoardBaseItemModel {
    private String _id;
    private String cellType;
    private String content;

    /**
     * This constructor is used for client to make a new cell
     * The _id will be over-written when it's pushed to server
     */
    public BoardBaseItemModel(String content, String cellType) {
        this.content = content;
        this.cellType = cellType;
        this._id = "server will recreate it";
    }

    public BoardBaseItemModel(String _id, String content, String cellType) {
        this._id = _id;
        this.content = content;
        this.cellType = cellType;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCellType() {
        return cellType;
    }

    public void setCellType(String cellType) {
        this.cellType = cellType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
