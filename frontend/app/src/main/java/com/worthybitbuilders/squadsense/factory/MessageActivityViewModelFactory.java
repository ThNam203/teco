package com.worthybitbuilders.squadsense.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.worthybitbuilders.squadsense.viewmodels.MessageActivityViewModel;

public class MessageActivityViewModelFactory implements ViewModelProvider.Factory {
    private final String chatRoomId;

    public MessageActivityViewModelFactory(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MessageActivityViewModel.class)) {
            return (T) new MessageActivityViewModel(chatRoomId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
