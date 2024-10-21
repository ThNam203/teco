package com.worthybitbuilders.squadsense.utils;

import com.worthybitbuilders.squadsense.models.board_models.BoardBaseItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardColumnHeaderModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardContentModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardDateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardStatusItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTextItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUpdateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;

import java.util.ArrayList;
import java.util.List;

public class ProjectTemplates {
    public static List<BoardContentModel> sampleProjectContent() {
        List<String> rowTitles = new ArrayList<>();
        rowTitles.add("Nam");
        rowTitles.add("Dat");
        rowTitles.add("Khoi");
        rowTitles.add("Son");

        List<BoardColumnHeaderModel> columnTitles = new ArrayList<>();
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.User, "Avatar"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "Frontend"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Text, "Backend"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Text, "Fullstack"));

        List<List<BoardBaseItemModel>> cells = new ArrayList<>();

        List<String> statusContent = new ArrayList<>();
        statusContent.add("Lam chua xong");
        statusContent.add("Bat dau lam");
        statusContent.add("Lam gan xong");
        statusContent.add("Da xong");

        List<BoardBaseItemModel> firstRow = new ArrayList<>();
        firstRow.add(new BoardUserItemModel());
        firstRow.add(new BoardStatusItemModel("Lam chua xong", statusContent));
        firstRow.add(new BoardTextItemModel("Bat dau lam"));
        firstRow.add(new BoardTextItemModel("Lam gan xong"));

        List<BoardBaseItemModel> secondRow = new ArrayList<>();
        secondRow.add(new BoardUserItemModel());
        secondRow.add(new BoardStatusItemModel("Da xong", statusContent));
        secondRow.add(new BoardTextItemModel("Lam chua xong"));
        secondRow.add(new BoardTextItemModel("Bat dau lam"));

        List<BoardBaseItemModel> thirdRow = new ArrayList<>();
        thirdRow.add(new BoardUserItemModel());
        thirdRow.add(new BoardStatusItemModel("Lam gan xong", statusContent));
        thirdRow.add(new BoardTextItemModel("Da xong"));
        thirdRow.add(new BoardTextItemModel("Bat dau lam"));

        List<BoardBaseItemModel> fourthRow = new ArrayList<>();
        fourthRow.add(new BoardUserItemModel());
        fourthRow.add(new BoardStatusItemModel("Lam gan xong", statusContent));
        fourthRow.add(new BoardTextItemModel("Da xong"));
        fourthRow.add(new BoardTextItemModel("Bat dau lam"));

        cells.add(firstRow);
        cells.add(secondRow);
        cells.add(thirdRow);
        cells.add(fourthRow);

        List<BoardContentModel> projectContent = new ArrayList<>();
        projectContent.add(new BoardContentModel("Members", rowTitles, columnTitles, cells));
        return projectContent;
    }
}
