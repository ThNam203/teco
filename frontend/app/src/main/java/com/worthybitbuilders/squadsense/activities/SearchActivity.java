package com.worthybitbuilders.squadsense.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.adapters.ProjectAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivitySearchBinding;
import com.worthybitbuilders.squadsense.databinding.MinimizeProjectMoreOptionsBinding;
import com.worthybitbuilders.squadsense.models.MinimizedProjectModel;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.MainActivityViewModel;
import com.worthybitbuilders.squadsense.viewmodels.ProjectActivityViewModel;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    private ProjectAdapter projectAdapter;
    private MainActivityViewModel viewModel;
    private UserViewModel userViewModel;
    private ProjectActivityViewModel projectActivityViewModel;
    private List<MinimizedProjectModel> listMinimizeProject = new ArrayList<>();
    List<MinimizedProjectModel> tempListMinimizeProject = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        viewModel = new ViewModelProvider(SearchActivity.this).get(MainActivityViewModel.class);
        userViewModel = new ViewModelProvider(SearchActivity.this).get(UserViewModel.class);
        projectActivityViewModel = new ViewModelProvider(SearchActivity.this).get(ProjectActivityViewModel.class);

        binding.rvSearchingProject.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.super.onBackPressed();
                finish();
            }
        });

        binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String toSearch = binding.inputSearch.getText().toString();

                if(!toSearch.isEmpty())
                {
                    tempListMinimizeProject.clear();
                    listMinimizeProject.forEach(minimizedProjectModel -> {
                        if(minimizedProjectModel.getTitle().toLowerCase().startsWith(toSearch.toLowerCase()))
                            tempListMinimizeProject.add(minimizedProjectModel);
                    });

                    binding.rvSearchingProject.setAdapter(projectAdapter);

                    if(tempListMinimizeProject.size() > 0)
                    {
                        binding.layoutNoResult.setVisibility(View.GONE);
                        binding.rvSearchingProject.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        binding.layoutNoResult.setVisibility(View.VISIBLE);
                        binding.rvSearchingProject.setVisibility(View.GONE);
                    }
                }
                else
                {
                    tempListMinimizeProject.clear();
                    binding.rvSearchingProject.setAdapter(projectAdapter);

                    binding.layoutNoResult.setVisibility(View.VISIBLE);
                    binding.rvSearchingProject.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadData();
    }

    private void LoadData()
    {
        projectAdapter = new ProjectAdapter(
                tempListMinimizeProject,
                _id -> {
                    SharedPreferencesManager.saveData(SharedPreferencesManager.KEYS.CURRENT_PROJECT_ID, _id);
                    saveRecentAccessProject(_id);
                    Intent intent = new Intent(SearchActivity.this, ProjectActivity.class);
                    intent.putExtra("whatToDo", "fetch");
                    intent.putExtra("projectId", _id);
                    startActivity(intent);
                },
                (view, _id) -> showMinimizeProjectOptions(view, _id));

        viewModel.getAllProjects(new MainActivityViewModel.GetProjectsFromRemoteHandlers() {
            @Override
            public void onSuccess(List<MinimizedProjectModel> dataMinimizeProjects) {
                listMinimizeProject.clear();
                listMinimizeProject.addAll(dataMinimizeProjects);
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(SearchActivity.this, message, Toast.LENGTH_SHORT);
            }
        });
    }

    private void saveRecentAccessProject(String projectId)
    {
        if(projectId == null) return;
        userViewModel.saveRecentProjectId(projectId, new UserViewModel.DefaultCallback() {
            @Override
            public void onSuccess() {
                //just save recent access and do no thing when success
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(SearchActivity.this, message, Toast.LENGTH_SHORT);
            }
        });
    }

    private void showMinimizeProjectOptions(View anchor, String projectId) {
        MinimizeProjectMoreOptionsBinding binding = MinimizeProjectMoreOptionsBinding.inflate(getLayoutInflater());
        PopupWindow popupWindow = new PopupWindow(binding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setElevation(50);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        int xOffset = anchor.getWidth(); // Offset from the right edge of the anchor view
        int yOffset = - anchor.getHeight() / 2;
        popupWindow.showAsDropDown(anchor, xOffset, yOffset);

        binding.btnDeleteProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleConfirmDialog = "Delete";
                String contentConfirmDialog = "Do you want to delete this project ?";
                DialogUtils.showConfirmDialogDelete(SearchActivity.this, titleConfirmDialog, contentConfirmDialog, new DialogUtils.ConfirmAction() {
                    @Override
                    public void onAcceptToDo(Dialog thisDialog) {
                        thisDialog.dismiss();
                        projectActivityViewModel.deleteProject(projectId, new ProjectActivityViewModel.ApiCallHandlers() {
                            @Override
                            public void onSuccess() {
                                popupWindow.dismiss();
                                ToastUtils.showToastSuccess(SearchActivity.this, "Project deleted", Toast.LENGTH_SHORT);
                                LoadData();
                            }

                            @Override
                            public void onFailure(String message) {
                                ToastUtils.showToastError(SearchActivity.this, "You are not allowed to delete this project", Toast.LENGTH_SHORT);
                            }
                        });

                    }

                    @Override
                    public void onCancel(Dialog thisDialog) {
                        thisDialog.dismiss();
                    }
                });
            }
        });
    }
}