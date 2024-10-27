package com.worthybitbuilders.squadsense.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.worthybitbuilders.squadsense.viewmodels.BoardDetailItemViewModel;

public class BoardItemDetailViewModelFactory implements ViewModelProvider.Factory {
    private final int rowPosition;

    public BoardItemDetailViewModelFactory(int rowPosition) {
        this.rowPosition = rowPosition;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BoardDetailItemViewModel.class)) {
            return (T) new BoardDetailItemViewModel(rowPosition);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

