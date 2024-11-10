package com.worthybitbuilders.squadsense.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.activities.EditProfileActivity;
import com.worthybitbuilders.squadsense.databinding.ConfirmDialogDeleteBinding;
import com.worthybitbuilders.squadsense.databinding.ConfirmDialogYesNoBinding;

import java.util.Calendar;

public class DialogUtils {
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

    public static Dialog showConfirmDialogDelete(Context context,String title, String content, ConfirmAction action) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ConfirmDialogDeleteBinding binding = ConfirmDialogDeleteBinding.inflate(LayoutInflater.from(context), null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        binding.tvTitle.setText(title);
        binding.tvAdditionalContent.setText(content);

        binding.btnConfirm.setOnClickListener(view -> action.onAcceptToDo(dialog));
        binding.btnCancel.setOnClickListener(view -> action.onCancel(dialog));

        dialog.show();
        return dialog;
    }

    public static Dialog showConfirmDialogYesNo(Context context,String title, String content, ConfirmAction action) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ConfirmDialogYesNoBinding binding = ConfirmDialogYesNoBinding.inflate(LayoutInflater.from(context), null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);

        binding.tvTitle.setText(title);
        binding.tvAdditionalContent.setText(content);

        binding.btnYes.setOnClickListener(view -> action.onAcceptToDo(dialog));
        binding.btnNo.setOnClickListener(view -> action.onCancel(dialog));

        dialog.show();
        return dialog;
    }

    public interface ConfirmAction{
        void onAcceptToDo(Dialog thisDialog);
        void onCancel(Dialog thisDialog);
    }
}
