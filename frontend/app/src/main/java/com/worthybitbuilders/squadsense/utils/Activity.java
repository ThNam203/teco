package com.worthybitbuilders.squadsense.utils;

import android.content.Context;
import android.content.Intent;

public class Activity {
    public static void switchToActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }
}
