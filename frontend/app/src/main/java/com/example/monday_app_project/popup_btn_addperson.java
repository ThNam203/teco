package com.example.monday_app_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class popup_btn_addperson extends AppCompatActivity {

    EditText inputEmail = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_btn_addperson);
        inputEmail = (EditText) findViewById(R.id.input_email);
    }

    @Override
    public void onStart() {
        super.onStart();
        // set focus on the edit text when the dialog is shown
        inputEmail.requestFocus();
    }
}