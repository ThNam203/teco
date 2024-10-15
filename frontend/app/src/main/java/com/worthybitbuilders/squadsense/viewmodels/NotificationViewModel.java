package com.worthybitbuilders.squadsense.viewmodels;


import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.worthybitbuilders.squadsense.models.ErrorResponse;
import com.worthybitbuilders.squadsense.models.Notification;
import com.worthybitbuilders.squadsense.services.NotificationService;
import com.worthybitbuilders.squadsense.services.RetrofitServices;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationViewModel extends ViewModel {
    private final NotificationService notificationService = RetrofitServices.getNotificationService();

    public NotificationViewModel() {}

    public void getNotification (String userId, getNotificationCallback callback) {
        Call<List<Notification>> result = notificationService.getNotificationByReceiverId(userId);
        result.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful()) {
                    List<Notification> listNotification = response.body();
                    callback.onSuccess(listNotification);
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
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void deleteNotification (String notificationId, deleteNotificationCallback callback) {
        Call<Void> result = notificationService.deleteNotificationById(notificationId);
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


    public interface getNotificationCallback {
        public void onSuccess(List<Notification> notificationData);
        public void onFailure(String message);
    }
    public interface deleteNotificationCallback {
        public void onSuccess();
        public void onFailure(String message);
    }
}
