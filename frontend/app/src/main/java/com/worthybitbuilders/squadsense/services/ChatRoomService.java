package com.worthybitbuilders.squadsense.services;

import com.worthybitbuilders.squadsense.models.ChatMessage;
import com.worthybitbuilders.squadsense.models.ChatRoom;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatRoomService {
    @GET("{userId}/chatroom")
    Call<List<ChatRoom>> getAllChatRoom(@Path("userId") String userId);

    @GET("{userId}/chatroom/{chatRoomId}")
    Call<List<ChatMessage>> getAllMessageInChatRoom(@Path("userId") String userId, @Path("chatRoomId") String chatRoomId);

    @POST("{userId}/chatroom")
    Call<ChatRoom> createNewChatRoom(@Path("userId") String userId, @Body JSONObject memberIds);
}
