package com.worthybitbuilders.squadsense.services;

import com.worthybitbuilders.squadsense.models.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NotificationService {
    @GET("notification/{receiverId}")
    Call<List<Notification>> getNotificationByReceiverId(@Path("receiverId") String receiverId);

    @GET("delete-notification/{notificationId}")
    Call<Void> deleteNotificationById(@Path("notificationId") String notificationId);
}