package com.worthybitbuilders.squadsense.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String SHARED_PREFS_NAME = "MySharedPrefs";
    public enum KEYS {
        USERID("userid"),
        JWT("jwt");

        private final String key;

        KEYS(String key) {
            this.key = key;
        }

        private String getKey() {
            return key;
        }
    }

    private static SharedPreferencesManager instance;
    private static SharedPreferences sharedPreferences;

    public static void init(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    public static void saveData(KEYS key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key.getKey(), value);
        editor.apply();
    }

    public static String getData(KEYS key) {
        return sharedPreferences.getString(key.getKey(), "");
    }

    public static void clearData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
