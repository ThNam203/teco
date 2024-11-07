package com.worthybitbuilders.squadsense.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.activities.MessagingActivity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import pub.devrel.easypermissions.EasyPermissions;

public class NotificationUtil {
    private static final String MESSAGE_CHANNEL_NAME = "MESSAGE_CHANNEL_NAME";
    private static final String MESSAGE_CHANNEL_ID = "MESSAGE_CHANNEL_ID";
    private static final String MESSAGE_CHANNEL_DESCRIPTION = "Show messages sent from other users";

    @SuppressLint("MissingPermission")
    public static void createNewMessageNotification(Context context, String username, String message, String chatRoomId) {
        // check permission for android 13
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                !EasyPermissions.hasPermissions(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                )
        ) return;

        Intent intent = new Intent(context, MessagingActivity.class);
        intent.putExtra("chatRoomId", chatRoomId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, MESSAGE_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(username)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // chatRoomId is the notification id so that it can update later
        notificationManager.notify(stringToNumber(chatRoomId), notification);
    }

    /** Should only be called within MyApplication.java */
    public static void createNotificationChannels(Application application) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // make sound when notify
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(
                    MESSAGE_CHANNEL_ID,
                    MESSAGE_CHANNEL_NAME,
                    importance
            );
            channel.setDescription(MESSAGE_CHANNEL_DESCRIPTION);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = application.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static int stringToNumber(String string) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hashBytes = digest.digest(string.getBytes(StandardCharsets.UTF_8));
        int value = 0;
        for (int i = 0; i < 4; i++) {
            value <<= 8;
            value |= (hashBytes[i] & 0xFF);
        }
        return value;
    }
}
