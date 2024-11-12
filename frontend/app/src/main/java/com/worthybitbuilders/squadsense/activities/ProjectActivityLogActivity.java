package com.worthybitbuilders.squadsense.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.adapters.ActivityLogAdapter;
import com.worthybitbuilders.squadsense.adapters.activityLogFilterAdapter.FilterBoardAdapter;
import com.worthybitbuilders.squadsense.adapters.activityLogFilterAdapter.FilterCreatorAdapter;
import com.worthybitbuilders.squadsense.adapters.activityLogFilterAdapter.FilterDateAdapter;
import com.worthybitbuilders.squadsense.adapters.activityLogFilterAdapter.FilterTypeAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityProjectActivityLogBinding;
import com.worthybitbuilders.squadsense.databinding.PopupFilterActivityLogBinding;
import com.worthybitbuilders.squadsense.models.ActivityLog;
import com.worthybitbuilders.squadsense.services.ProjectService;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.utils.CustomUtils;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectActivityLogActivity extends AppCompatActivity {
    ProjectService projectService = RetrofitServices.getProjectService();
    ActivityProjectActivityLogBinding binding;
    List<ActivityLog> listActivityLog = new ArrayList<>();
    List<ActivityLog> listSearchActivityLog = new ArrayList<>();
    List<ActivityLog> listFilterActivityLog = new ArrayList<>();
    List<String> listSelectedType = new ArrayList<>();
    List<String> listSelectedDate = new ArrayList<>();
    List<ActivityLog.ActivityLogCreator> listSelectedCreator = new ArrayList<>();
    List<ActivityLog.ActivityLogBoard> listSelectedBoard = new ArrayList<>();

    boolean isSearching = false;
    boolean isFiltering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectActivityLogBinding.inflate(getLayoutInflater());
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(binding.getRoot());

        binding.rvActivityLogs.setLayoutManager(new LinearLayoutManager(this));

        binding.btnBack.setOnClickListener(view -> finish());
        binding.btnSearch.setOnClickListener(view -> {
            if(binding.inputSearch.getVisibility() == View.GONE)
            {
                binding.inputSearch.setVisibility(View.VISIBLE);
                binding.btnSearch.setImageResource(R.drawable.ic_close);
            }
            else
            {
                binding.inputSearch.setVisibility(View.GONE);
                binding.btnSearch.setImageResource(R.drawable.ic_search);
                closeCurrentKeyboardOnDevice();
            }
        });

        binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listSearchActivityLog.clear();
                if(isFiltering) listSearchActivityLog.addAll(listFilterActivityLog);
                else listSearchActivityLog.addAll(listActivityLog);

                String toSearch = binding.inputSearch.getText().toString();

                if(toSearch.isEmpty())
                {
                    Drawable[] drawables = binding.inputSearch.getCompoundDrawables();
                    binding.inputSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(drawables[0], drawables[1], null, drawables[3]);

                    //if user is not searching, change background of btn with this color
                    isSearching = false;
                    int color = ContextCompat.getColor(ProjectActivityLogActivity.this, R.color.chosen_color);
                    binding.btnSearch.setBackgroundTintList(ColorStateList.valueOf(color));
                }
                else {
                    Drawable[] drawables = binding.inputSearch.getCompoundDrawables();
                    Drawable clearDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_close);
                    binding.inputSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(drawables[0], drawables[1], clearDrawable, drawables[3]);

                    //if user is searching, change background of btn with this color
                    isSearching = true;
                    int color = ContextCompat.getColor(ProjectActivityLogActivity.this, R.color.orange);
                    binding.btnSearch.setBackgroundTintList(ColorStateList.valueOf(color));
                }

                List<ActivityLog> toRemove = new ArrayList<>();
                listSearchActivityLog.forEach(activityLog ->
                {
                    if(!activityLog.getDescription().toLowerCase().contains(toSearch.toLowerCase())) toRemove.add(activityLog);
                });

                toRemove.forEach(item -> listSearchActivityLog.remove(item));
                ActivityLogAdapter adapter = new ActivityLogAdapter(listSearchActivityLog);
                binding.rvActivityLogs.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.inputSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(binding.inputSearch.getCompoundDrawables()[2] == null) return false;
                    if (event.getRawX() >= (binding.inputSearch.getRight() - binding.inputSearch.getCompoundDrawables()[2].getBounds().width())) {
                        // Người dùng đã chạm vào nút xóa
                        binding.inputSearch.setText(""); // Xóa toàn bộ chữ trong EditText
                        return true;
                    }
                }
                return false;
            }
        });

        binding.btnFilter.setOnClickListener(view -> showFilterPopup());

        LoadData();
    }

    private void LoadData()
    {
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
                    listActivityLog = response.body();
                    ActivityLogAdapter adapter = new ActivityLogAdapter(listActivityLog);
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

    private void closeCurrentKeyboardOnDevice()
    {
        View rootView = getWindow().getDecorView().getRootView();
        InputMethodManager imm = (InputMethodManager) getSystemService(ProjectActivityLogActivity.this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
    }

    private void showFilterPopup() {
        final Dialog dialog = new Dialog(ProjectActivityLogActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        PopupFilterActivityLogBinding popupFilterBinding = PopupFilterActivityLogBinding.inflate(getLayoutInflater());
        dialog.setContentView(popupFilterBinding.getRoot());

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        List<String> tempSelectedType = new ArrayList<>(listSelectedType);
        List<String> tempSelectedDate = new ArrayList<>(listSelectedDate);
        List<ActivityLog.ActivityLogCreator> tempSelectedCreator = new ArrayList<>(listSelectedCreator);
        List<ActivityLog.ActivityLogBoard> tempSelectedBoard = new ArrayList<>(listSelectedBoard);

        loadListFilterType(popupFilterBinding, tempSelectedType);
        loadListFilterDate(popupFilterBinding, tempSelectedDate);
        loadListFilterCreator(popupFilterBinding, tempSelectedCreator);
        loadListFilterBoard(popupFilterBinding, tempSelectedBoard);

        popupFilterBinding.btnClosePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        popupFilterBinding.btnDone.setOnClickListener(view -> {
            clearFilter();
            listSelectedType.addAll(tempSelectedType);
            listSelectedDate.addAll(tempSelectedDate);
            listSelectedCreator.addAll(tempSelectedCreator);
            listSelectedBoard.addAll(tempSelectedBoard);

            listFilterActivityLog.clear();
            if(isSearching) listFilterActivityLog.addAll(listSearchActivityLog);
            else listFilterActivityLog.addAll(listActivityLog);

            if(listSelectedType.size() > 0) filterType(listFilterActivityLog);
            if(listSelectedDate.size() > 0) filterDate(listFilterActivityLog);
            if(listSelectedCreator.size() > 0) filterCreator(listFilterActivityLog);
            if(listSelectedBoard.size() > 0) filterBoard(listFilterActivityLog);

            if(listSelectedType.size() > 0 || listSelectedCreator.size() > 0 || listSelectedDate.size() > 0 || listSelectedBoard.size() > 0) {
                //if user is filtering, change background of btn with this color
                isFiltering = true;
                int color = ContextCompat.getColor(ProjectActivityLogActivity.this, R.color.orange);
                binding.btnFilter.setBackgroundTintList(ColorStateList.valueOf(color));
            }
            else
            {
                //if user is not filtering, change background of btn with this color
                isFiltering = false;
                int color = ContextCompat.getColor(ProjectActivityLogActivity.this, R.color.chosen_color);
                binding.btnFilter.setBackgroundTintList(ColorStateList.valueOf(color));
            }

            ActivityLogAdapter adapter = new ActivityLogAdapter(listFilterActivityLog);
            binding.rvActivityLogs.setAdapter(adapter);

            dialog.dismiss();
        });

        popupFilterBinding.btnClear.setOnClickListener(view ->
        {
            tempSelectedType.clear();
            tempSelectedDate.clear();
            tempSelectedCreator.clear();
            tempSelectedBoard.clear();

            loadListFilterType(popupFilterBinding, tempSelectedType);
            loadListFilterDate(popupFilterBinding, tempSelectedDate);
            loadListFilterCreator(popupFilterBinding, tempSelectedCreator);
            loadListFilterBoard(popupFilterBinding, tempSelectedBoard);
        });
    }

    private void filterType(List<ActivityLog> list)
    {
        List<ActivityLog> toRemove = new ArrayList<>();
        list.forEach(activityLog -> {
            if(!listSelectedType.contains(activityLog.getType()))
                toRemove.add(activityLog);
        });
        toRemove.forEach(item -> list.remove(item));
    }

    private void filterDate(List<ActivityLog> list)
    {
        List<ActivityLog> toRemove = new ArrayList<>();
        list.forEach(activityLog -> {
            String date = CustomUtils.mongooseDateToFormattedString(activityLog.getCreatedDate());
            if(!listSelectedDate.contains(date)) toRemove.add(activityLog);
        });
        toRemove.forEach(item -> list.remove(item));
    }

    private void filterCreator(List<ActivityLog> list)
    {
        List<ActivityLog> toRemove = new ArrayList<>();
        List<String> listSelectedCreatorId = new ArrayList<>();
        listSelectedCreator.forEach(creator -> {
            if(!listSelectedCreatorId.contains(creator._id)) listSelectedCreatorId.add(creator._id);
        });
        list.forEach(activityLog -> {
            if(!listSelectedCreatorId.contains(activityLog.getCreator()._id))
                toRemove.add(activityLog);
        });
        toRemove.forEach(item -> list.remove(item));
    }

    private void filterBoard(List<ActivityLog> list)
    {
        List<ActivityLog> toRemove = new ArrayList<>();
        list.forEach(activityLog -> {
            if(activityLog.getBoard() != null)
            {
                if(!listSelectedBoard.contains(activityLog.getBoard()))
                    toRemove.add(activityLog);
            }
        });
        toRemove.forEach(item -> list.remove(item));
    }

    private void clearFilter()
    {
        listSelectedType.clear();
        listSelectedDate.clear();
        listSelectedCreator.clear();
        listSelectedBoard.clear();
    }

    private void loadListFilterType(PopupFilterActivityLogBinding popupFilterBinding, List<String> tempSelectedType)
    {
        popupFilterBinding.rvFilterType.setLayoutManager(new LinearLayoutManager(ProjectActivityLogActivity.this, LinearLayoutManager.HORIZONTAL, false));
        List<String> listType = new ArrayList<>();
        if(isSearching)
        {
            listSearchActivityLog.forEach(activityLog -> {
                if(!listType.contains(activityLog.getType()))
                    listType.add(activityLog.getType());
            });
        }
        else
        {
            listActivityLog.forEach(activityLog -> {
                if(!listType.contains(activityLog.getType()))
                    listType.add(activityLog.getType());
            });
        }
        FilterTypeAdapter filterTypeAdapter = new FilterTypeAdapter(listType, tempSelectedType);
        popupFilterBinding.rvFilterType.setAdapter(filterTypeAdapter);
    }

    private void loadListFilterDate(PopupFilterActivityLogBinding popupFilterBinding, List<String> tempSelectedDate)
    {
        popupFilterBinding.rvFilterDate.setLayoutManager(new LinearLayoutManager(ProjectActivityLogActivity.this, LinearLayoutManager.HORIZONTAL, false));
        List<String> listDate = new ArrayList<>();
        if(isSearching)
        {
            listSearchActivityLog.forEach(activityLog -> {
                String date = CustomUtils.mongooseDateToFormattedString(activityLog.getCreatedDate());
                if(!listDate.contains(date)) listDate.add(date);
            });
        }
        else
        {
            listActivityLog.forEach(activityLog -> {
                String date = CustomUtils.mongooseDateToFormattedString(activityLog.getCreatedDate());
                if(!listDate.contains(date)) listDate.add(date);
            });
        }

        FilterDateAdapter filterDateAdapter = new FilterDateAdapter(listDate, tempSelectedDate);
        popupFilterBinding.rvFilterDate.setAdapter(filterDateAdapter);
    }

    private void loadListFilterCreator(PopupFilterActivityLogBinding popupFilterBinding, List<ActivityLog.ActivityLogCreator> tempSelectedCreator)
    {
        popupFilterBinding.rvFilterCreator.setLayoutManager(new LinearLayoutManager(ProjectActivityLogActivity.this, LinearLayoutManager.HORIZONTAL, false));
        List<ActivityLog.ActivityLogCreator> listCreator = new ArrayList<>();
        List<String> listCreatorId = new ArrayList<>();

        if(isSearching)
        {
            listSearchActivityLog.forEach(activityLog -> {
                if(!listCreatorId.contains(activityLog.getCreator()._id)) {
                    listCreatorId.add(activityLog.getCreator()._id);
                    listCreator.add(activityLog.getCreator());
                }
            });
        }
        else {
            listActivityLog.forEach(activityLog -> {
                if(!listCreatorId.contains(activityLog.getCreator()._id)) {
                    listCreatorId.add(activityLog.getCreator()._id);
                    listCreator.add(activityLog.getCreator());
                }
            });
        }


        FilterCreatorAdapter filterCreatorAdapter = new FilterCreatorAdapter(listCreator, tempSelectedCreator);
        popupFilterBinding.rvFilterCreator.setAdapter(filterCreatorAdapter);
    }

    private void loadListFilterBoard(PopupFilterActivityLogBinding popupFilterBinding, List<ActivityLog.ActivityLogBoard> tempSelectedBoard)
    {
        popupFilterBinding.rvFilterBoard.setLayoutManager(new LinearLayoutManager(ProjectActivityLogActivity.this, LinearLayoutManager.HORIZONTAL, false));
        List<ActivityLog.ActivityLogBoard> listBoard = new ArrayList<>();
        List<String> listBoardId = new ArrayList<>();
        if(isSearching)
        {
            listSearchActivityLog.forEach(activityLog -> {
                if(activityLog.getBoard() != null)
                {
                    if(!listBoardId.contains(activityLog.getBoard()._id)) {
                        listBoardId.add(activityLog.getBoard()._id);
                        listBoard.add(activityLog.getBoard());
                    }
                }
            });
        }
        else
        {
            listActivityLog.forEach(activityLog -> {
                if(activityLog.getBoard() != null)
                {
                    if(!listBoardId.contains(activityLog.getBoard()._id)) {
                        listBoardId.add(activityLog.getBoard()._id);
                        listBoard.add(activityLog.getBoard());
                    }
                }
            });
        }


        FilterBoardAdapter filterBoardAdapter = new FilterBoardAdapter(listBoard, tempSelectedBoard);
        popupFilterBinding.rvFilterBoard.setAdapter(filterBoardAdapter);
    }
}