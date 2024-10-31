package com.worthybitbuilders.squadsense.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.worthybitbuilders.squadsense.R;

public class TeamActivity extends AppCompatActivity {

    ImageButton btnBack = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        getSupportActionBar().hide();

        //Init variables here
        btnBack = (ImageButton) findViewById(R.id.btn_back);

        //set onclick buttons here
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeamActivity.super.onBackPressed();
            }
        });
    }
}