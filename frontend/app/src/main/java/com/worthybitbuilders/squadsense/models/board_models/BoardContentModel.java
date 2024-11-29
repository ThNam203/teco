package com.worthybitbuilders.squadsense.models.board_models;

import java.util.List;

public class BoardContentModel {
    private String _id;
    private String boardTitle;
    private List<BoardRowHeaderModel> rowCells;
    private List<BoardColumnHeaderModel> columnCells;
    private Integer deadlineColumnIndex = null;
    // TODO: COLUMN SHOULD HOLD THE COLUMN TYPE, NOT THE CELL
    private List<List<BoardBaseItemModel>> cells;

    /** This constructor is used for when data is fetched from server */
    public BoardContentModel(String _id, String boardTitle, List<BoardRowHeaderModel> rowCells, List<BoardColumnHeaderModel> columnCells, List<List<BoardBaseItemModel>> cells, int deadlineColumnIndex) {
        this._id = _id;
        this.boardTitle = boardTitle;
        this.rowCells = rowCells;
        this.columnCells = columnCells;
        this.cells = cells;
        this.deadlineColumnIndex = deadlineColumnIndex;
    }

    /**
     * This constructor is used for client to make a new board
     * The _id will be over-written when it's pushed and returned from server
     */
    public BoardContentModel(String boardTitle, List<BoardRowHeaderModel> rowCells, List<BoardColumnHeaderModel> columnCells, List<List<BoardBaseItemModel>> cells) {
        this._id = "server will recreate it later";
        this.boardTitle = boardTitle;
        this.rowCells = rowCells;
        this.columnCells = columnCells;
        this.cells = cells;
        this.deadlineColumnIndex = null;
    }

    public String get_id() {
        return _id;
    }

    public String getBoardTitle() {
        return boardTitle;
    }

    public void setBoardTitle(String boardTitle) {
        this.boardTitle = boardTitle;
    }

    public List<BoardRowHeaderModel> getRowCells() {
        return rowCells;
    }

    public void setRowCells(List<BoardRowHeaderModel> rowCells) {
        this.rowCells = rowCells;
    }

    public List<BoardColumnHeaderModel> getColumnCells() {
        return columnCells;
    }

    public void setColumnCells(List<BoardColumnHeaderModel> columnCells) {
        this.columnCells = columnCells;
    }

    public List<List<BoardBaseItemModel>> getCells() {
        return cells;
    }

    public void setCells(List<List<BoardBaseItemModel>> cells) {
        this.cells = cells;
    }

    public Integer getDeadlineColumnIndex() {
        return deadlineColumnIndex;
    }

    public void setDeadlineColumnIndex(Integer deadlineColumnIndex) {
        this.deadlineColumnIndex = deadlineColumnIndex;
    }
}
