package com.worthybitbuilders.squadsense.services;

import com.worthybitbuilders.squadsense.models.FriendRequest;
import com.worthybitbuilders.squadsense.models.UserModel;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FriendService {
    @POST("create-request")
    Call<FriendRequest> createRequest(@Body FriendRequest friendRequest);

    @POST("reply-request/{response}")
    Call<String> replyRequest(@Path("response") String response,@Body FriendRequest friendRequest);

    @GET("get-friend/{userId}")
    Call<List<UserModel>> getFriendById(@Path("userId") String userId);

    @DELETE("{userId}/friend/{unfriendUserId}")
    Call<Void> deleteFriend(@Path("userId") String userId, @Path("unfriendUserId") String unfriendUserId);
}