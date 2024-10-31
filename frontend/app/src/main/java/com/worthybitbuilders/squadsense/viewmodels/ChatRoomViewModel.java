package com.worthybitbuilders.squadsense.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.worthybitbuilders.squadsense.models.ChatRoom;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.services.UserService;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomViewModel extends ViewModel {
    private List<ChatRoom> chatRooms;
    private final MutableLiveData<List<ChatRoom>> chatRoomsLiveData = new MutableLiveData<>(null);
    private final UserService userService = RetrofitServices.getUserService();
    public ChatRoomViewModel() {}

    public void getChatRooms(ApiCallHandler handler) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        userService.getAllChatroom(userId).enqueue(new Callback<List<ChatRoom>>() {
            @Override
            public void onResponse(@NonNull Call<List<ChatRoom>> call, @NonNull Response<List<ChatRoom>> response) {
                if (response.isSuccessful()) {
                    chatRooms = response.body();
                    chatRoomsLiveData.setValue(chatRooms);
                    handler.onSuccess();
                } else {
                    handler.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ChatRoom>> call, @NonNull Throwable t) {
                handler.onFailure(t.getMessage());
            }
        });
    }

    public MutableLiveData<List<ChatRoom>> getChatRoomsLiveData() {
        return chatRoomsLiveData;
    }

    public interface ApiCallHandler {
        void onSuccess();
        void onFailure(String message);
    }
}
