package com.worthybitbuilders.squadsense.factory;

import com.worthybitbuilders.squadsense.models.board_models.BoardBaseItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardCheckboxItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardColumnHeaderModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardDateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardEmptyItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardNumberItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardStatusItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTextItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTimelineItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUpdateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;

import java.util.ArrayList;

public class BoardItemFactory {
    public static BoardBaseItemModel createNewItem(BoardColumnHeaderModel.ColumnType type) {
        BoardBaseItemModel newItem = null;
        if (type == BoardColumnHeaderModel.ColumnType.NewColumn) {
            newItem = new BoardEmptyItemModel();
        } else if (type == BoardColumnHeaderModel.ColumnType.Status) {
            newItem = new BoardStatusItemModel();
        } else if (type == BoardColumnHeaderModel.ColumnType.Update) {
            newItem = new BoardUpdateItemModel();
        } else if (type == BoardColumnHeaderModel.ColumnType.Text) {
            newItem = new BoardTextItemModel("");
        } else if (type == BoardColumnHeaderModel.ColumnType.Number) {
            newItem = new BoardNumberItemModel("");
        } else if (type == BoardColumnHeaderModel.ColumnType.TimeLine) {
            newItem = new BoardTimelineItemModel();
        } else if (type == BoardColumnHeaderModel.ColumnType.Date) {
            newItem = new BoardDateItemModel();
        } else if (type == BoardColumnHeaderModel.ColumnType.User) {
            newItem = new BoardUserItemModel();
        } else if (type == BoardColumnHeaderModel.ColumnType.Checkbox) {
            newItem = new BoardCheckboxItemModel(false);
        }

        return newItem;
    }
}
