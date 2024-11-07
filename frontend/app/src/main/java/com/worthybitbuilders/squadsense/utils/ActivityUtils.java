package com.worthybitbuilders.squadsense.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;

import androidx.core.app.ActivityOptionsCompat;

import com.worthybitbuilders.squadsense.R;

public class ActivityUtils {
    public static void switchToActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }
}
