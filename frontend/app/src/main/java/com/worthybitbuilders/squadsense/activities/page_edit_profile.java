package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;

import com.worthybitbuilders.squadsense.R;

public class page_edit_profile extends AppCompatActivity {

    ImageButton btnBack = null;
    ImageButton btnCamera = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_edit_profile);

        getSupportActionBar().hide();

        //Init variables here
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnCamera = (ImageButton) findViewById(R.id.btn_camera);


        //set onclick buttons here
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page_edit_profile.super.onBackPressed();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCamera_showPopup();
            }
        });

    }

    //define function here
    private void btnCamera_showPopup() {
        final Dialog dialog = new Dialog(getWindow().getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_of_camera);
        //Set activity of button in dialog here


        //
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
    }
}