package com.example.monday_app_project.Util;

import android.content.Context;
import android.content.Intent;

public class SwitchActivity {
    public static void switchToActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }
}
