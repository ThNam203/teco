package com.worthybitbuilders.squadsense.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.worthybitbuilders.squadsense.models.ChatMessage;
import com.worthybitbuilders.squadsense.models.ChatMessageRequest;
import com.worthybitbuilders.squadsense.models.ChatRoom;
import com.worthybitbuilders.squadsense.services.ChatRoomService;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.SocketClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivityViewModel extends ViewModel {
    // see more in SocketClient.java to know about its utility
    // basically remove the new message notification if the message is in this room
    public static String currentChatRoomId = "";
    private final ArrayList<ChatMessage> mMessageList = new ArrayList<>();
    // the purpose is only to notify about the update
    private final MutableLiveData<String> newMessageLiveData = new MutableLiveData<>(null);
    private final String chatRoomId;
    private final String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
    private final Socket socket = SocketClient.getInstance();
    private final ChatRoomService chatRoomService = RetrofitServices.getChatRoomService();
    private ChatRoom chatRoom;
    private final Emitter.Listener onNewMessage = args -> {
        ChatMessage newMessage = new Gson().fromJson(args[0].toString(), ChatMessage.class);
        mMessageList.add(newMessage);
        // the purpose is only to notify about the update
        newMessageLiveData.postValue("yea a new value");
    };

    public MessageActivityViewModel(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public void sendNewMessage(String messageContent, List<ChatMessage.MessageFile> files) {
        ChatMessageRequest newMessage = new ChatMessageRequest(chatRoomId, messageContent, userId, files);
        String jsonData = new Gson().toJson(newMessage);
        socket.emit("newMessage", jsonData);
    }

    public Call<ChatRoom> getChatRoomInfor() {
        return chatRoomService.getAChatRoom(userId, chatRoomId);
    }

    // TODO: REMOVE THE SET CHATROOM
    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void getAllMessage(ApiCallHandler handler) {
        chatRoomService.getAllMessageInChatRoom(userId, chatRoomId).enqueue(new Callback<List<ChatMessage>>() {
            @Override
            public void onResponse(@NonNull Call<List<ChatMessage>> call, @NonNull Response<List<ChatMessage>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) mMessageList.addAll(response.body());
                    handler.onSuccess();
                } else handler.onFailure(response.message());
            }

            @Override
            public void onFailure(@NonNull Call<List<ChatMessage>> call, @NonNull Throwable t) {
                handler.onFailure(t.getMessage());
            }
        });
    }

    public void deleteMemberFromGroup(String deleteMemberId, ApiCallHandler handler) {
        chatRoomService.removeMemberFromGroupChat(userId, chatRoomId, deleteMemberId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    chatRoom.getMembers().removeIf(member -> (member._id.equals(deleteMemberId)));
                    handler.onSuccess();
                } else handler.onFailure(response.message());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handler.onFailure(t.getMessage());
            }
        });
    }

//    public void addMemberToGroup(String newMemberId, ApiCallHandler handler) throws JSONException {
//        JSONObject data = new JSONObject();
//        data.put("newMemberId", newMemberId);
//        chatRoomService.addMemberToGroupChat(userId, chatRoomId, data).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    chatRoom.getMembers().add()));
//                    handler.onSuccess();
//                } else handler.onFailure(response.message());
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                handler.onFailure(t.getMessage());
//            }
//        });
//    }

    public ArrayList<ChatMessage> getMessageList() {
        return mMessageList;
    }

    public void changeSocketEventsOnEnter() {
        // this makes sure user to join a room, because user can create a new chatroom
        currentChatRoomId = chatRoomId;
        socket.emit("joinChatRoom", chatRoomId);
        socket.on("newMessage", onNewMessage);
    }

    public void changeSocketEventsOnLeave() {
        currentChatRoomId = "";
        socket.emit("leaveChatRoom");
        socket.off("newMessage");
    }

    public MutableLiveData<String> getNewMessageLiveData() {
        return newMessageLiveData;
    }

    public interface ApiCallHandler {
        void onSuccess();
        void onFailure(String message);
    }

    public void startGroupCall() {
        JSONObject offer = new JSONObject();
        try {
            String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
            offer.put("callerId", userId);
            offer.put("chatRoomId", chatRoomId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        socket.emit("offerGroupCall", offer);
    }
}
