package com.worthybitbuilders.squadsense.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.worthybitbuilders.squadsense.models.board_models.BoardRowHeaderModel;
import com.worthybitbuilders.squadsense.viewmodels.BoardDetailItemViewModel;

public class BoardItemDetailViewModelFactory implements ViewModelProvider.Factory {
    private final int rowPosition;
    private final String projectId;
    private final String boardId;
    private final String cellId;
    private final String projectTitle;
    private final String boardTitle;
    private final BoardRowHeaderModel rowHeaderModel;
    public BoardItemDetailViewModelFactory(int rowPosition, String projectId, String boardId, String cellId, String projectTitle, String boardTitle, BoardRowHeaderModel rowHeaderModel) {
        this.rowPosition = rowPosition;
        this.projectId = projectId;
        this.boardId = boardId;
        this.cellId = cellId;
        this.projectTitle = projectTitle;
        this.boardTitle = boardTitle;
        this.rowHeaderModel = rowHeaderModel;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BoardDetailItemViewModel.class)) {
            return (T) new BoardDetailItemViewModel(rowPosition, projectId, boardId, cellId, projectTitle, boardTitle, rowHeaderModel);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

