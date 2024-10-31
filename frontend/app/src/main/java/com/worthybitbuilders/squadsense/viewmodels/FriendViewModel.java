package com.worthybitbuilders.squadsense.viewmodels;


import android.util.Patterns;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.worthybitbuilders.squadsense.models.ErrorResponse;
import com.worthybitbuilders.squadsense.models.FriendRequest;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.services.FriendService;
import com.worthybitbuilders.squadsense.services.RetrofitServices;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendViewModel extends ViewModel {
    private final FriendService friendService = RetrofitServices.getFriendService();

    public FriendViewModel() {}

    public boolean IsValidEmail(String email)
    {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void createRequest (String senderId, String receiverId, FriendRequestCallback callback) {
        Call<FriendRequest> result = friendService.createRequest(new FriendRequest(senderId, receiverId));
        result.enqueue(new Callback<FriendRequest>() {
            @Override
            public void onResponse(Call<FriendRequest> call, Response<FriendRequest> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                }
                else {
                    ErrorResponse err = null;
                    try {
                        err = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                    } catch (IOException e) {
                        callback.onFailure("Something has gone wrong!");
                    }
                    callback.onFailure(err.getMessage());
                }
            }

            @Override
            public void onFailure(Call<FriendRequest> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void reply (String senderId, String reveiverId, String response, FriendRequestCallback callback) {
        Call<String> result = friendService.replyRequest(response, new FriendRequest(senderId, reveiverId));
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                }
                else {
                    ErrorResponse err = null;
                    try {
                        err = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                    } catch (IOException e) {
                        callback.onFailure("Something has gone wrong!");
                    }
                    callback.onFailure(err.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void getFriendById (String userId, getFriendCallback callback) {
        Call<List<UserModel>> result = friendService.getFriendById(userId);
        result.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                if (response.isSuccessful()) {
                    List<UserModel> listFriends = response.body();
                    callback.onSuccess(listFriends);
                }
                else {
                    ErrorResponse err = null;
                    try {
                        err = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                    } catch (IOException e) {
                        callback.onFailure("Something has gone wrong!");
                    }
                    callback.onFailure(err.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<UserModel>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }


    public interface FriendRequestCallback {
        public void onSuccess();
        public void onFailure(String message);
    }

    public interface getFriendCallback {
        public void onSuccess(List<UserModel> friends);
        public void onFailure(String message);
    }
}
