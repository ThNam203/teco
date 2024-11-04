package com.worthybitbuilders.squadsense.viewmodels;

import android.util.Patterns;

import androidx.lifecycle.ViewModel;

import com.auth0.android.jwt.JWT;
import com.google.gson.Gson;
import com.worthybitbuilders.squadsense.models.ErrorResponse;
import com.worthybitbuilders.squadsense.models.LoginRequest;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.services.UserService;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {
    private final UserService userService = RetrofitServices.getUserService();

    public LoginViewModel() {}

    public boolean isValidEmail(String email)
    {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isAutoLogging() {
        String jwt = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.JWT);
        if (jwt.isEmpty()) return false;
        return true;
    }

    public void logIn (String email, String password, LogInCallback callback) {
        Call<String> login = userService.loginUser(new LoginRequest(email, password));
        login.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String jwtString = response.body();
                    SharedPreferencesManager.saveData(SharedPreferencesManager.KEYS.JWT, jwtString);
                    JWT jwt = new JWT(jwtString);
                    String userId = jwt.getClaim("id").asString();
                    SharedPreferencesManager.saveData(SharedPreferencesManager.KEYS.USER_ID, userId);
                    callback.onSuccess(userId);
                }
                else {
                    ErrorResponse err = null;
                    try {
                        err = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
                    } catch (Exception e) {
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

    public interface LogInCallback {
        public void onSuccess(String userId);
        public void onFailure(String message);
    }
}
