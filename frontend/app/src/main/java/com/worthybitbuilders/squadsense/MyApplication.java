package com.worthybitbuilders.squadsense;

import android.app.Application;

import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesManager.init(this);
    }
}
