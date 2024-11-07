package com.worthybitbuilders.squadsense.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.worthybitbuilders.squadsense.viewmodels.BoardDetailItemViewModel;
import com.worthybitbuilders.squadsense.viewmodels.ProjectActivityViewModel;

public class ProjectActivityViewModelFactory implements ViewModelProvider.Factory {
    private final String projectId;

    public ProjectActivityViewModelFactory(String projectId) {
        this.projectId = projectId;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProjectActivityViewModel.class)) {
            return (T) new ProjectActivityViewModel(projectId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
