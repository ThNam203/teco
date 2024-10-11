package com.worthybitbuilders.squadsense.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.worthybitbuilders.squadsense.R;

public class WorkFragment extends Fragment {

    Button btnHideDoneItem = null;
    Boolean IsbtnHideDoneItemChecked = false;

    ImageButton btnAddItem = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_work, container, false);

        //Init variables here
        btnHideDoneItem = (Button) v.findViewById(R.id.btn_hide_done_item);
        btnAddItem = v.findViewById(R.id.btn_add_item);

        //set onclick buttons here
        btnHideDoneItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(IsbtnHideDoneItemChecked == false)
                {
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_checkbox_checked);
                    drawable.setColorFilter(ContextCompat.getColor(getContext(), R.color.chosen_color), PorterDuff.Mode.SRC_IN);
                    btnHideDoneItem.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

                    IsbtnHideDoneItemChecked = true;
                }
                else
                {
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_checkbox_unchecked);
                    drawable.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary_icon_color), PorterDuff.Mode.SRC_IN);
                    btnHideDoneItem.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

                    IsbtnHideDoneItemChecked = false;
                }

            }
        });
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddItem_showDialog();
            }
        });
        return v;
    }

    //define function here
    private void btnAddItem_showDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_add_new_item);

        //Set activity of button in dialog here


        //


        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
        ImageButton btnClosePopup = (ImageButton) dialog.findViewById(R.id.btn_close_popup);
        btnClosePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}