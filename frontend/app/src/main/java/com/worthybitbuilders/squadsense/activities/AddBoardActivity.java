package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.worthybitbuilders.squadsense.R;

public class AddBoardActivity extends AppCompatActivity {
    ImageButton btnClose = null;
    Button btnNewBoard = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_board);
        getSupportActionBar().hide();

        //Init variables here
        btnNewBoard = (Button) findViewById(R.id.btn_create_new_board);
        btnClose = (ImageButton) findViewById(R.id.btn_close);

        //set onclick buttons here
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddBoardActivity.super.onBackPressed();
            }
        });
        btnNewBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent boardIntent = new Intent(page_add_board.this, ProjectActivity.class);
                boardIntent.putExtra("whatToDo", "createNew");
                finish();
                startActivity(boardIntent);
            }
        });
    }
}