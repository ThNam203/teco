package com.worthybitbuilders.squadsense.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.worthybitbuilders.squadsense.models.ChatRoom;
import com.worthybitbuilders.squadsense.services.ChatRoomService;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.services.UserService;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomViewModel extends ViewModel {
    private final List<ChatRoom> chatRooms = new ArrayList<>();
    private final ChatRoomService chatRoomService = RetrofitServices.getChatRoomService();

    public ChatRoomViewModel() {}

    public void getChatRoomsRemotely(ApiCallHandler handler) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        chatRoomService.getAllChatRoom(userId).enqueue(new Callback<List<ChatRoom>>() {
            @Override
            public void onResponse(@NonNull Call<List<ChatRoom>> call, @NonNull Response<List<ChatRoom>> response) {
                if (response.isSuccessful()) {
                    chatRooms.clear();
                    if (response.body() != null) chatRooms.addAll(response.body());
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

    public void createNewChatRoom(List<String> memberIds, boolean isGroup, String title, ApiCallHandler handler) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        JSONObject data = new JSONObject();
        try {
            data.put("memberIds", memberIds);
            data.put("isGroup", isGroup);
            data.put("title", title);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        chatRoomService.createNewChatRoom(userId, data).enqueue(new Callback<ChatRoom>() {
            @Override
            public void onResponse(@NonNull Call<ChatRoom> call, @NonNull Response<ChatRoom> response) {
                if (response.isSuccessful()) {
                    chatRooms.add(0, response.body());
                    handler.onSuccess();
                } else handler.onFailure(response.message());
            }

            @Override
            public void onFailure(@NonNull Call<ChatRoom> call, @NonNull Throwable t) {
                handler.onFailure(t.getMessage());
            }
        });
    }

    public List<ChatRoom> getChatRooms() { return chatRooms; }

    public interface ApiCallHandler {
        void onSuccess();
        void onFailure(String message);
    }
}
