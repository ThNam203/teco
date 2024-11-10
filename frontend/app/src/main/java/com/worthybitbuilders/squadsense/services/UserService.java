package com.worthybitbuilders.squadsense.services;

import com.worthybitbuilders.squadsense.models.ChatRoom;
import com.worthybitbuilders.squadsense.models.LoginRequest;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.models.board_models.ProjectModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserService {
    @GET("{userId}/get-user-by-id")
    Call<UserModel> getUserById(@Path("userId") String userId);

    @GET("{userId}/get-user-by-email/{email}")
    Call<UserModel> getUserByEmail(@Path("userId") String userId, @Path("email") String email);
    @GET("{userId}/get-all-users")
    Call<List<UserModel>> getAllUsers(@Path("userId") String userId);
    @POST("{userId}/save-recent-project-id/{projectId}")
    Call<Void> saveRecentProjectIds(@Path("userId") String userId, @Path("projectId") String projectId);

    @GET("{userId}/get-recent-project-id")
    Call<List<String>> getRecentProjectIds(@Path("userId") String userId);
    @GET("{userId}/get-my-own-project-id")
    Call<List<String>> getMyOwnProjectIds(@Path("userId") String userId);
    @Multipart
    @POST("{userId}/upload-avatar")
    Call<ResponseBody> uploadAvatarFile(@Path("userId") String userId, @Part MultipartBody.Part avatarFile);

    @POST("update-user")
    Call<UserModel> updateUser(@Body UserModel user);

    @POST("signup")
    Call<Void> createNewUser(@Body UserModel newUser);

    @POST("login")
    Call<String> loginUser(@Body LoginRequest loginRequest);
}