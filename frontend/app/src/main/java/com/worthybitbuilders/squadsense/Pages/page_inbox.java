package com.worthybitbuilders.squadsense.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.worthybitbuilders.squadsense.R;

public class page_inbox extends AppCompatActivity {

    ImageButton btnBack = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_inbox);
        getSupportActionBar().hide();

        //Init variables here
        btnBack = (ImageButton) findViewById(R.id.btn_back);

        //set onclick buttons here
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page_inbox.super.onBackPressed();
            }
        });
    }
}