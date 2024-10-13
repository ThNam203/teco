package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.worthybitbuilders.squadsense.R;

public class NotificationSettingActivity extends AppCompatActivity {

    ImageButton btnBack = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_setting);
        getSupportActionBar().hide();

        //init variables here
        btnBack = (ImageButton) findViewById(R.id.btn_back);

        //set onclick buttons here
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationSettingActivity.super.onBackPressed();
            }
        });
    }
}