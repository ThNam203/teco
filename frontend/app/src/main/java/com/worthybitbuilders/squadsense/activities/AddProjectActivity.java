package com.worthybitbuilders.squadsense.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.worthybitbuilders.squadsense.databinding.ActivityAddProjectBinding;

public class AddProjectActivity extends AppCompatActivity {
    ActivityAddProjectBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.ITManagement.setOnClickListener(view -> {
            Intent boardIntent = new Intent(AddProjectActivity.this, ProjectActivity.class);
            boardIntent.putExtra("templateName", "ITManagement");
            boardIntent.putExtra("whatToDo", "createNew");
            finish();
            startActivity(boardIntent);
        });

        binding.FacilitiesRequests.setOnClickListener(view -> {
            Intent boardIntent = new Intent(AddProjectActivity.this, ProjectActivity.class);
            boardIntent.putExtra("templateName", "FacilitiesRequests");
            boardIntent.putExtra("whatToDo", "createNew");
            finish();
            startActivity(boardIntent);
        });

        binding.ProjectRequestAndApprovals.setOnClickListener(view -> {
            Intent boardIntent = new Intent(AddProjectActivity.this, ProjectActivity.class);
            boardIntent.putExtra("templateName", "ProjectRequestAndApprovals");
            boardIntent.putExtra("whatToDo", "createNew");
            finish();
            startActivity(boardIntent);
        });
        binding.RecruitmentAndOnboarding.setOnClickListener(view -> {
            Intent boardIntent = new Intent(AddProjectActivity.this, ProjectActivity.class);
            boardIntent.putExtra("templateName", "RecruitmentAndOnboarding");
            boardIntent.putExtra("whatToDo", "createNew");
            finish();
            startActivity(boardIntent);
        });

        binding.btnClose.setOnClickListener(view -> AddProjectActivity.super.onBackPressed());
        binding.btnCreateNewBoard.setOnClickListener(view -> {
            Intent boardIntent = new Intent(AddProjectActivity.this, ProjectActivity.class);
            boardIntent.putExtra("templateName", "defaultTemplate");
            boardIntent.putExtra("whatToDo", "createNew");
            finish();
            startActivity(boardIntent);
        });
    }
}