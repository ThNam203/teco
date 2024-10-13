package com.worthybitbuilders.squadsense.services;

import com.worthybitbuilders.squadsense.models.LoginRequest;
import com.worthybitbuilders.squadsense.models.UserModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {
    @GET("{userId}")
    Call<UserModel> getUser(@Path("userId") String userId);

    @POST("{userId}")
    Call<UserModel> updateUser(@Path("userId") String userId);

    @POST("signup")
    Call<Void> createNewUser(@Body UserModel newUser);

    @POST("login")
    Call<String> loginUser(@Body LoginRequest loginRequest);
}
