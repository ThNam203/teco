package com.worthybitbuilders.squadsense.services;

import com.worthybitbuilders.squadsense.models.LoginRequest;
import com.worthybitbuilders.squadsense.models.UserModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {
    @GET("user-id/{userId}")
    Call<UserModel> getUserById(@Path("userId") String userId);

    @GET("user-email/{email}")
    Call<UserModel> getUserByEmail(@Path("email") String email);

    @POST("update-user")
    Call<UserModel> updateUser(@Body UserModel user);

    @POST("signup")
    Call<Void> createNewUser(@Body UserModel newUser);

    @POST("login")
    Call<String> loginUser(@Body LoginRequest loginRequest);
}