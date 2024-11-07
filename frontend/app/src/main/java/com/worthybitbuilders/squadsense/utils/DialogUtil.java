package com.worthybitbuilders.squadsense.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.worthybitbuilders.squadsense.R;

public class DialogUtil {
    public static Dialog GetLoadingDialog(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_loading_dialog, null, false);
        dialog.setContentView(dialogView);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
        return dialog;
    }

    public static Dialog showConfirmDialog(Context context,String title, String content, ConfirmAction action) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.confirm_dialog, null, false);
        dialog.setContentView(dialogView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        TextView btnConfirm = (TextView) dialog.findViewById(R.id.btnConfirm);
        TextView btnCancel = (TextView) dialog.findViewById(R.id.btnCancel);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
        TextView tvContent = (TextView) dialog.findViewById(R.id.tvAdditionalContent);

        tvTitle.setText(title);
        tvContent.setText(content);

        btnConfirm.setOnClickListener(view -> action.onAcceptToDo(dialog));
        btnCancel.setOnClickListener(view -> action.onCancel(dialog));

        dialog.show();
        return dialog;
    }

    public interface ConfirmAction{
        void onAcceptToDo(Dialog thisDialog);
        void onCancel(Dialog thisDialog);
    }
}
