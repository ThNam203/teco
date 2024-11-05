package com.worthybitbuilders.squadsense;

import android.Manifest;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.worthybitbuilders.squadsense.utils.NotificationUtil;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.SocketClient;

import pub.devrel.easypermissions.EasyPermissions;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesManager.init(this);
        NotificationUtil.createNotificationChannels(this);

        /**
         *  If userId is empty, it means the user has not logged in
         *  We must attempt initialize in the login activity
         */
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        if (!userId.isEmpty()) SocketClient.InitializeIO(this, userId);
    }
}
