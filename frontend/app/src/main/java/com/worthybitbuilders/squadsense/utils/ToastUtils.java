package com.worthybitbuilders.squadsense.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.worthybitbuilders.squadsense.R;

import java.time.Duration;

public class ToastUtils {
    public static void showToastSuccess(Context context, String message, int duration) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_toast_success, null);

        // Set the message text
        TextView textView = view.findViewById(R.id.toast_message);
        textView.setText(message);

        // Create and show the toast
        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(view);
        toast.setGravity(Gravity.TOP, 0, 50);
        toast.show();
    }
    public static void showToastError(Context context, String message, int duration) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_toast_error, null);

        // Set the message text
        TextView textView = view.findViewById(R.id.toast_message);
        textView.setText(message);

        // Create and show the toast
        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(view);
        toast.setGravity(Gravity.TOP, 0, 50);
        toast.show();
    }
}
