package com.worthybitbuilders.squadsense.services;

import com.worthybitbuilders.squadsense.models.FriendRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FriendService {
    @POST("create-request")
    Call<FriendRequest> createRequest(@Body FriendRequest friendRequest);

    @POST("reply-request/{response}")
    Call<String> replyRequest(@Path("response") String response,@Body FriendRequest friendRequest);
}