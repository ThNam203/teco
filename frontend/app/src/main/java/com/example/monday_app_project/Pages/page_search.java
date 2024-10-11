package com.example.monday_app_project.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.monday_app_project.Fragments.HomeFragment;
import com.example.monday_app_project.R;
import com.example.monday_app_project.Util.SwitchActivity;

public class page_search extends AppCompatActivity {

    ImageButton btnBack = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_search);
        getSupportActionBar().hide();


        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page_search.super.onBackPressed();
//                finish();
            }
        });
    }
}