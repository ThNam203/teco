
package com.worthybitbuilders.squadsense.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.worthybitbuilders.squadsense.viewmodels.ProjectActivityViewModel;
import com.worthybitbuilders.squadsense.viewmodels.UpdateTaskCommentViewModel;

public class UpdateTaskCommentViewModelFactory implements ViewModelProvider.Factory {
    private final String projectId;
    private final String boardId;
    private final String cellId;
    private final String updateTaskId;

    public UpdateTaskCommentViewModelFactory(String projectId, String boardId, String cellId, String updateTaskId) {
        this.projectId = projectId;
        this.boardId = boardId;
        this.cellId = cellId;
        this.updateTaskId = updateTaskId;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UpdateTaskCommentViewModel.class)) {
            return (T) new UpdateTaskCommentViewModel(projectId, boardId, cellId, updateTaskId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

