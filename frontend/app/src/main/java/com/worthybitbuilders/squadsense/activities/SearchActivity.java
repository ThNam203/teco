package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.worthybitbuilders.squadsense.R;

public class SearchActivity extends AppCompatActivity {

    ImageButton btnBack = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();


        //init variables here
        btnBack = findViewById(R.id.btn_back);

        //set onclick buttons here
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.super.onBackPressed();
//                finish();
            }
        });
    }
}