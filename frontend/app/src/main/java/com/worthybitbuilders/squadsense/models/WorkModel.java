package com.worthybitbuilders.squadsense.models;

import android.content.Intent;

import com.worthybitbuilders.squadsense.utils.CompareUtils;
import com.worthybitbuilders.squadsense.utils.ConvertUtils;

import java.util.Date;

public class WorkModel {
    private final String projectId;
    private final String projectTitle;
    private final String boardId;
    private final String boardTitle;
    private final Integer boardPosition;
    private final String rowTitle;
    private final Integer cellRowPosition;
    private final boolean isDone;

    public WorkModel(String projectId, String projectTitle, String boardId, String boardTitle, Integer boardPosition, String rowTitle, Integer cellRowPosition, boolean isDone) {
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.boardId = boardId;
        this.boardTitle = boardTitle;
        this.boardPosition = boardPosition;
        this.rowTitle = rowTitle;
        this.cellRowPosition = cellRowPosition;
        this.isDone = isDone;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public String getBoardId() {
        return boardId;
    }

    public String getBoardTitle() {
        return boardTitle;
    }

    public Integer getBoardPosition() {
        return boardPosition;
    }

    public String getRowTitle() {
        return rowTitle;
    }

    public Integer getCellRowPosition() {
        return cellRowPosition;
    }

    public boolean isDone() {
        return isDone;
    }
}
