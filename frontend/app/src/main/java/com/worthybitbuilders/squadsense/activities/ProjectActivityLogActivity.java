package com.worthybitbuilders.squadsense.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.adapters.ActivityLogAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityProjectActivityLogBinding;
import com.worthybitbuilders.squadsense.models.ActivityLog;
import com.worthybitbuilders.squadsense.services.ProjectService;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectActivityLogActivity extends AppCompatActivity {
    ProjectService projectService = RetrofitServices.getProjectService();
    ActivityProjectActivityLogBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectActivityLogBinding.inflate(getLayoutInflater());
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(view -> finish());
        binding.rvActivityLogs.setLayoutManager(new LinearLayoutManager(this));

        Dialog loadingDialog = DialogUtils.GetLoadingDialog(this);
        loadingDialog.show();
        Intent getIntent = getIntent();
        projectService.getActivityLogs(
                SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID),
                getIntent.getStringExtra("projectId")
        ).enqueue(new Callback<List<ActivityLog>>() {
            @Override
            public void onResponse(@NonNull Call<List<ActivityLog>> call, @NonNull Response<List<ActivityLog>> response) {
                if (response.isSuccessful()) {
                    ActivityLogAdapter adapter = new ActivityLogAdapter(response.body());
                    binding.rvActivityLogs.setAdapter(adapter);
                } else {
                    ToastUtils.showToastError(ProjectActivityLogActivity.this, "Unable to get activity logs, please try again", Toast.LENGTH_SHORT);
                }

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<List<ActivityLog>> call, @NonNull Throwable t) {
                ToastUtils.showToastError(ProjectActivityLogActivity.this, "Unable to get activity logs, please try again", Toast.LENGTH_SHORT);
                loadingDialog.dismiss();
            }
        });
    }
}