package com.worthybitbuilders.squadsense.viewmodels;


import android.util.Patterns;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.worthybitbuilders.squadsense.models.ErrorResponse;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.models.board_models.ProjectModel;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.services.UserService;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;

import java.io.IOException;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends ViewModel {
    private final UserService userService = RetrofitServices.getUserService();

    public UserViewModel() {}

    public boolean IsValidEmail(String email)
    {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void getUserById (String userId, UserCallback callback) {
        Call<UserModel> result = userService.getUserById(userId);
        result.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful()) {
                    UserModel user = response.body();
                    callback.onSuccess(user);
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
            public void onFailure(Call<UserModel> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void getUserByEmail (String email, UserCallback callback) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<UserModel> result = userService.getUserByEmail(userId, email);
        result.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful()) {
                    UserModel user = response.body();
                    callback.onSuccess(user);
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
            public void onFailure(Call<UserModel> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void updateUser (UserModel user, UserCallback callback) {
        Call<UserModel> result = userService.updateUser(user);
        result.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful()) {
                    UserModel user = response.body();
                    callback.onSuccess(user);
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
            public void onFailure(Call<UserModel> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void uploadAvatar (String userId, MultipartBody.Part avatarFilePart, DefaultCallback callback) {
        Call<ResponseBody> result = userService.uploadAvatarFile(userId, avatarFilePart);
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void saveRecentProjectId (String projectId, DefaultCallback callback) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<Void> result = userService.saveRecentProjectIds(userId, projectId);
        result.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
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
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void getRecentProjectId (RecentProjectIdsCallback callback) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<List<String>> result = userService.getRecentProjectIds(userId);
        result.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    List<String> data = response.body();
                    callback.onSuccess(data);
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
            public void onFailure(Call<List<String>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void getMyOwnProject(ApiCallMyOwnProjectsHandlers handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<List<String>> call = userService.getMyOwnProjectIds(userId);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    List<String> myOwnProjectIds = response.body();
                    handlers.onSuccess(myOwnProjectIds);
                } else {
                    ErrorResponse err = null;
                    try {
                        err = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                    } catch (IOException e) {
                        handlers.onFailure("Something has gone wrong!");
                    }
                    handlers.onFailure(err.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }

    public void getAllUser (CallListUserHandlers callback) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<List<UserModel>> result = userService.getAllUsers(userId);
        result.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                if (response.isSuccessful()) {
                    List<UserModel> user = response.body();
                    callback.onSuccess(user);
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

    public interface UserCallback {
        void onSuccess(UserModel user);
        void onFailure(String message);
    }
    public interface CallListUserHandlers {
        void onSuccess(List<UserModel> dataUsers);
        void onFailure(String message);
    }
    public interface RecentProjectIdsCallback {
        void onSuccess(List<String> dataRecentProjectIds);
        void onFailure(String message);
    }
    public interface DefaultCallback {
        void onSuccess();
        void onFailure(String message);
    }
    public interface ApiCallMyOwnProjectsHandlers {
        void onSuccess(List<String> dataMyOwnProjects);
        void onFailure(String message);
    }
}
