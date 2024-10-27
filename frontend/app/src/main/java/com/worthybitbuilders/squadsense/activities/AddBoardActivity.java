package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import com.worthybitbuilders.squadsense.R;

public class AddBoardActivity extends AppCompatActivity {
    ImageButton btnClose = null;
    Button btnNewBoard = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        getSupportActionBar().hide();

        btnNewBoard = (Button) findViewById(R.id.btn_create_new_board);
        btnClose = (ImageButton) findViewById(R.id.btn_close);

        btnClose.setOnClickListener(view -> AddBoardActivity.super.onBackPressed());
        btnNewBoard.setOnClickListener(view -> {
            Intent boardIntent = new Intent(AddBoardActivity.this, ProjectActivity.class);
            boardIntent.putExtra("whatToDo", "createNew");
            finish();
            startActivity(boardIntent);
        });
    }
}