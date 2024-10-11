package com.worthybitbuilders.squadsense.Util;

import android.content.Context;
import android.content.Intent;

import com.worthybitbuilders.squadsense.R;

public class SwitchActivity {
    public static void switchToActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }
}
