package com.example.monday_app_project;


import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class HomeFragment extends Fragment {

    RelativeLayout layout = null;
    Button btn_myfavorities = null;
    ImageButton btn_addperson = null;
    ImageButton btn_add = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        btn_myfavorities = v.findViewById(R.id.btn_myfavorites);
        btn_addperson = v.findViewById(R.id.btn_addperson);
        btn_add = v.findViewById(R.id.btn_add);
        layout = v.findViewById(R.id.home_fragment);

        btn_myfavorities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_myfavorities_showDialog();
            }
        });

        btn_addperson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_addperson_showDialog();

            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPopupBtnAdd();
            }
        });
        // Inflate the layout for this fragment
        return v;
    }

    private void setPopupBtnAdd() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_btn_add, null);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        PopupWindow popupBtnAdd = new PopupWindow(popupView,width,height, true);
        popupBtnAdd.setAnimationStyle(R.style.PopupAnimation);
        layout.post(new Runnable() {
            @Override
            public void run() {
                popupBtnAdd.showAtLocation(layout, Gravity.RIGHT, 0, 550);
            }
        });

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupBtnAdd.dismiss();
                return true;
            }
        });
    }


    private void btn_addperson_showDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_btn_addperson);

        //Set activity of button in dialog here


        //


        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
        ImageButton btnClosePopupBtnAddPerson = (ImageButton) dialog.findViewById(R.id.btn_close_popup);
        btnClosePopupBtnAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void btn_myfavorities_showDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_btn_myfavortites);
        //Set activity of button in dialog here


        //
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        ImageButton btnClosePopupBtnMyfavorites = (ImageButton) dialog.findViewById(R.id.btn_close_popup);
        btnClosePopupBtnMyfavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}