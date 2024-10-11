package com.worthybitbuilders.squadsense.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.Util.SwitchActivity;

public class page_profile extends AppCompatActivity {

    ImageButton btnBack = null;
    Button btnEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_profile);
        getSupportActionBar().hide();


        //Init variables here
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnEdit = (Button) findViewById(R.id.btn_edit);


        //set onclick buttons here
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page_profile.super.onBackPressed();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnEdit_showActivity();
            }
        });
    }

    private void btnEdit_showActivity() {
        SwitchActivity.switchToActivity(getWindow().getContext(), page_edit_profile.class);
    }
}