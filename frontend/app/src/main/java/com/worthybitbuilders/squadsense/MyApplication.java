package com.worthybitbuilders.squadsense;

import android.app.Application;
import android.widget.Toast;

import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.SocketUtil;

import java.net.URISyntaxException;

import io.socket.client.Socket;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesManager.init(this);

        /**
         *  If userId is empty, it means the user has not logged in
         *  We must attempt initialize in the login activity
         */
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        if (!userId.isEmpty()) {
            SocketUtil.InitializeIO(userId);
        }
    }
}
