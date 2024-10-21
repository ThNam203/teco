package com.worthybitbuilders.squadsense.models;

import com.worthybitbuilders.squadsense.models.board_models.BoardBaseItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardColumnHeaderModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardRowHeaderModel;

import java.util.List;

public class NewRowRequestModel {
    BoardRowHeaderModel rowHeaderModel;
    List<BoardBaseItemModel> cells;

    public NewRowRequestModel(BoardRowHeaderModel rowHeaderModel, List<BoardBaseItemModel> cells) {
        this.rowHeaderModel = rowHeaderModel;
        this.cells = cells;
    }

    public BoardRowHeaderModel getRowHeaderModel() {
        return rowHeaderModel;
    }

    public List<BoardBaseItemModel> getCells() {
        return cells;
    }

    public void setCells(List<BoardBaseItemModel> cells) {
        this.cells = cells;
    }
}
