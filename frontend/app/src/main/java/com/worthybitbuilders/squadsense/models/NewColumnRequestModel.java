package com.worthybitbuilders.squadsense.models;

import com.worthybitbuilders.squadsense.models.board_models.BoardBaseItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardColumnHeaderModel;

import java.util.List;

/**
 * This class is for REQUEST @BODY
 */
public class NewColumnRequestModel {
    BoardColumnHeaderModel columnHeaderModel;
    List<BoardBaseItemModel> cells;

    public NewColumnRequestModel(BoardColumnHeaderModel columnHeaderModel, List<BoardBaseItemModel> cells) {
        this.columnHeaderModel = columnHeaderModel;
        this.cells = cells;
    }

    public BoardColumnHeaderModel getColumnHeaderModel() {
        return columnHeaderModel;
    }

    public List<BoardBaseItemModel> getCells() {
        return cells;
    }

    public void setCells(List<BoardBaseItemModel> cells) {
        this.cells = cells;
    }
}
