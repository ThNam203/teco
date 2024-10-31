package com.worthybitbuilders.squadsense.services;

import com.worthybitbuilders.squadsense.models.ChatRoom;
import com.worthybitbuilders.squadsense.models.LoginRequest;
import com.worthybitbuilders.squadsense.models.UserModel;

import java.io.File;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserService {
    @GET("user-id/{userId}")
    Call<UserModel> getUserById(@Path("userId") String userId);

    @GET("user-email/{email}")
    Call<UserModel> getUserByEmail(@Path("email") String email);

    @Multipart
    @POST("/upload-avatar/{userId}")
    Call<ResponseBody> uploadAvatarFile(@Path("userId") String userId, @Part MultipartBody.Part avatarFile);

    @POST("update-user")
    Call<UserModel> updateUser(@Body UserModel user);

    @GET("{userId}/chatroom")
    Call<List<ChatRoom>> getAllChatroom(@Path("userId") String userId);

    @POST("signup")
    Call<Void> createNewUser(@Body UserModel newUser);

    @POST("login")
    Call<String> loginUser(@Body LoginRequest loginRequest);
}