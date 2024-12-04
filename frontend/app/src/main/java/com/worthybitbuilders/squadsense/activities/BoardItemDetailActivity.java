package com.worthybitbuilders.squadsense.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ActivityBoardItemDetailBinding;
import com.worthybitbuilders.squadsense.databinding.ConfirmDeleteSecondaryBinding;
import com.worthybitbuilders.squadsense.databinding.RowMoreOptionsBinding;
import com.worthybitbuilders.squadsense.databinding.RowRenamePopupBinding;
import com.worthybitbuilders.squadsense.factory.BoardItemDetailViewModelFactory;
import com.worthybitbuilders.squadsense.fragments.BoardDetailColumnFragment;
import com.worthybitbuilders.squadsense.fragments.BoardDetailUpdateFragment;
import com.worthybitbuilders.squadsense.models.BoardDetailItemModel;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardBaseItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardRowHeaderModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUpdateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.BoardDetailItemViewModel;
import com.worthybitbuilders.squadsense.viewmodels.ProjectActivityViewModel;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class BoardItemDetailActivity extends AppCompatActivity implements BoardDetailColumnFragment.ItemClickHelper {
    // first is button reference, second is its id
    final private List<Pair<Button, String>> buttons = new ArrayList<>();
    private ActivityBoardItemDetailBinding activityBinding;
    private BoardDetailItemViewModel viewModel;
    private ProjectActivityViewModel projectActivityViewModel;
    private String currentChosenCellId = null;
    private String rowTitle = "";
    private int rowPosition;
    private boolean isDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = ActivityBoardItemDetailBinding.inflate(getLayoutInflater());
        Objects.requireNonNull(getSupportActionBar()).hide();

        // this button doesn't need an actual id
        // even though we must sync it with others that also appear below
        buttons.add(new Pair<>(activityBinding.btnShowColumns, "randombullshitgo"));
        activityBinding.btnBack.setOnClickListener(view -> this.onBackPressed());

        Intent intent = getIntent();
        boolean isFromUpdateColumn = intent.getBooleanExtra("isFromUpdateColumn", false);
        String projectId = intent.getStringExtra("projectId");
        String boardId = intent.getStringExtra("boardId");
        String rowTitle = intent.getStringExtra("rowTitle");
        isDone = intent.getBooleanExtra("isDone", false);
        String projectTitle = intent.getStringExtra("projectTitle");
        String boardTitle = intent.getStringExtra("boardTitle");

        if (isDone) activityBinding.doneTick.setVisibility(View.VISIBLE);

        // updateCellId is only for when user tap the update item
        // which is because there could be more than 1 update item
        // when open from board
        String updateCellId = intent.getStringExtra("updateCellId");
        String updateCellTitle = intent.getStringExtra("updateCellTitle");
        int rowPosition = intent.getIntExtra("rowPosition", -1);

        this.rowTitle = rowTitle;
        this.rowPosition = rowPosition;

        activityBinding.itemTitle.setText(rowTitle);

        BoardItemDetailViewModelFactory viewModelFactory = new BoardItemDetailViewModelFactory(rowPosition, projectId, boardId, updateCellId, projectTitle, boardTitle, new BoardRowHeaderModel(rowTitle, isDone, false));
        viewModel = new ViewModelProvider(this, viewModelFactory).get(BoardDetailItemViewModel.class);
        projectActivityViewModel = new ViewModelProvider(this).get(ProjectActivityViewModel.class);

        if (rowPosition == -1 || projectId.isEmpty() || boardId.isEmpty()) {
            ToastUtils.showToastError(this, "Something went wrong, please try again", Toast.LENGTH_LONG);
            finish();
        }

        Dialog loadingDialog = DialogUtils.GetLoadingDialog(this);
        loadingDialog.show();

        viewModel.getDataFromRemote(new BoardDetailItemViewModel.ApiCallHandler() {
            @Override
            public void onSuccess() {
                BoardDetailItemModel data = viewModel.getItemsLiveData().getValue();
                if (data == null) return;
                if (isFromUpdateColumn) changeAllButtonBackgroundToDefault();
                for (int i = 0; i < data.getCells().size(); i++) {
                    if (!Objects.equals(data.getCells().get(i).getCellType(), "CellUpdate")) continue;
                    View updateButton = LayoutInflater.from(BoardItemDetailActivity.this).inflate(R.layout.board_detail_item_update_button_view, null, false);
                    ((Button) updateButton).setText(data.getColumnTitles().get(i));
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(16, 0, 16, 0);
                    updateButton.setLayoutParams(layoutParams);
                    String cellId = data.getCells().get(i).get_id();
                    int finalI = i;

                    if (isFromUpdateColumn && Objects.equals(updateCellId, cellId)) {
                        DrawableCompat.setTint(updateButton.getBackground(), ContextCompat.getColor(BoardItemDetailActivity.this, R.color.primary_btn_color));
                        int colorTint = ContextCompat.getColor(BoardItemDetailActivity.this, R.color.white);
                        ((Button) updateButton).setTextColor(colorTint);
                        ((Button) updateButton).setCompoundDrawableTintList(ColorStateList.valueOf(colorTint));
                        changeToUpdateFragment(updateCellId, data.getColumnTitles().get(finalI), rowTitle);
                    }

                    updateButton.setOnClickListener(view -> {
                        if (Objects.equals(currentChosenCellId, cellId)) return;
                        changeAllButtonBackgroundToDefault();
                        DrawableCompat.setTint(updateButton.getBackground(), ContextCompat.getColor(BoardItemDetailActivity.this, R.color.primary_btn_color));
                        int colorTint = ContextCompat.getColor(BoardItemDetailActivity.this, R.color.white);
                        ((Button) updateButton).setTextColor(colorTint);
                        ((Button) updateButton).setCompoundDrawableTintList(ColorStateList.valueOf(colorTint));
                        changeToUpdateFragment(cellId, data.getColumnTitles().get(finalI), rowTitle);
                    });
                    buttons.add(new Pair<>((Button)updateButton, cellId));
                    activityBinding.buttonsContainer.addView(updateButton);
                }

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(BoardItemDetailActivity.this, message, Toast.LENGTH_LONG);
                loadingDialog.dismiss();
            }
        });

        projectActivityViewModel.getProjectById(projectId, new ProjectActivityViewModel.ApiCallHandlers() {
            @Override
            public void onSuccess() {
                String creatorId = projectActivityViewModel.getProjectModel().getCreatorId();
                List<String> listAdminId = projectActivityViewModel.getProjectModel().getAdminIds();
                loadViewWithSuitableRole(creatorId, listAdminId);
            }

            @Override
            public void onFailure(String message) {

            }
        });

        if (!isFromUpdateColumn) changeToColumnFragment();
        activityBinding.btnMoreOptions.setOnClickListener(view -> showMoreOptions());
        activityBinding.btnShowColumns.setOnClickListener(view -> changeToColumnFragment());
        setContentView(activityBinding.getRoot());
    }

    private void showMoreOptions() {
        RowMoreOptionsBinding moreOptionsBinding = RowMoreOptionsBinding.inflate(getLayoutInflater());
        PopupWindow popupWindow = new PopupWindow(moreOptionsBinding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);

        moreOptionsBinding.btnRename.setOnClickListener(view -> {
            showRenameDialog();
            popupWindow.dismiss();
        });

        moreOptionsBinding.btnRemove.setOnClickListener(view -> {
            showConfirmDelete();
            popupWindow.dismiss();
        });

        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        String creatorId = projectActivityViewModel.getProjectModel().getCreatorId();
        List<String> listAdminId = projectActivityViewModel.getProjectModel().getAdminIds();
        if (isDone) {
            moreOptionsBinding.btnChangeDone.setText("Mark as not done");
            moreOptionsBinding.btnRemove.setVisibility(View.GONE);
            moreOptionsBinding.btnRename.setVisibility(View.GONE);
        }
        else{
            moreOptionsBinding.btnChangeDone.setText("Mark as done");
            if(creatorId.equals(userId) || listAdminId.contains(userId)) {
                moreOptionsBinding.btnRename.setVisibility(View.VISIBLE);
                moreOptionsBinding.btnRemove.setVisibility(View.VISIBLE);
            }
            else {
                moreOptionsBinding.btnRename.setVisibility(View.GONE);
                moreOptionsBinding.btnRemove.setVisibility(View.GONE);
            }
        }

        moreOptionsBinding.btnChangeDone.setOnClickListener(view -> {
            try {
                viewModel.updateRowIsDone(!isDone, new BoardDetailItemViewModel.ApiCallHandler() {
                    @Override
                    public void onSuccess() {
                        isDone = !isDone;
                        if (isDone) activityBinding.doneTick.setVisibility(View.VISIBLE);
                        else activityBinding.doneTick.setVisibility(View.GONE);
                        BoardDetailColumnFragment boardDetailColumnFragment = (BoardDetailColumnFragment) getSupportFragmentManager().findFragmentByTag("BOARD_DETAIL_COLUMN_FRAGMENT");
                        BoardDetailUpdateFragment boardDetailUpdateFragment = (BoardDetailUpdateFragment) getSupportFragmentManager().findFragmentByTag("BOARD_DETAIL_UPDATE_FRAGMENT");
                        if(boardDetailColumnFragment != null) boardDetailColumnFragment.setDone(isDone);
                        if(boardDetailUpdateFragment != null) boardDetailUpdateFragment.setDone(isDone);
                        ToastUtils.showToastSuccess(BoardItemDetailActivity.this, "Updated", Toast.LENGTH_SHORT);
                        popupWindow.dismiss();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtils.showToastError(BoardItemDetailActivity.this, message, Toast.LENGTH_SHORT);
                        popupWindow.dismiss();
                    }
                });
            } catch (JSONException e) {
                ToastUtils.showToastError(BoardItemDetailActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT);
                popupWindow.dismiss();
            }
        });

        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(this.activityBinding.btnMoreOptions, 0, 0);
    }

    private void showConfirmDelete() {
        final Dialog confirmDialog = new Dialog(this);
        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ConfirmDeleteSecondaryBinding binding = ConfirmDeleteSecondaryBinding.inflate(getLayoutInflater());
        confirmDialog.setContentView(binding.getRoot());

        binding.tvTitle.setText(String.format(Locale.US, "Delete row \"%s\"?", viewModel.getRowHeaderModel().getTitle()));
        binding.tvAdditionalContent.setText("This row will be removed from the board");
        binding.btnCancel.setOnClickListener(view -> confirmDialog.dismiss());
        binding.btnConfirm.setOnClickListener(view -> {
            try {
                viewModel.deleteRow(new BoardDetailItemViewModel.ApiCallHandler() {
                    @Override
                    public void onSuccess() {
                        confirmDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtils.showToastError(BoardItemDetailActivity.this, message, Toast.LENGTH_SHORT);
                    }
                });
            } catch (JSONException e) {
                ToastUtils.showToastError(BoardItemDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
            }
        });

        confirmDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        confirmDialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        confirmDialog.getWindow().setGravity(Gravity.CENTER);
        confirmDialog.show();
    }

    private void showRenameDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        RowRenamePopupBinding binding = RowRenamePopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        binding.etTextItem.setText(viewModel.getRowHeaderModel().getTitle());
        binding.btnClosePopup.setOnClickListener((view) -> dialog.dismiss());

        binding.btnSaveTextItem.setOnClickListener(view -> {
            String newContent = String.valueOf(binding.etTextItem.getText());
            if (newContent.isEmpty()) {
                ToastUtils.showToastError(BoardItemDetailActivity.this, "Name should not be empty", Toast.LENGTH_SHORT);
                return;
            }

            try {
                viewModel.updateRowTitle(newContent, new BoardDetailItemViewModel.ApiCallHandler() {
                    @Override
                    public void onSuccess() {
                        activityBinding.itemTitle.setText(viewModel.getRowHeaderModel().getTitle());
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtils.showToastError(BoardItemDetailActivity.this, message, Toast.LENGTH_SHORT);
                    }
                });
            } catch (JSONException e) {
                ToastUtils.showToastError(BoardItemDetailActivity.this, "Unable to rename, please try again", Toast.LENGTH_SHORT);
            }
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void changeAllButtonBackgroundToDefault() {
        buttons.forEach(pair -> {
            DrawableCompat.setTint(pair.first.getBackground(), ContextCompat.getColor(BoardItemDetailActivity.this, R.color.primary_btn_second_color));
            int colorTint = ContextCompat.getColor(BoardItemDetailActivity.this, R.color.primary_word_color);
            pair.first.setTextColor(colorTint);
            pair.first.setCompoundDrawableTintList(ColorStateList.valueOf(colorTint));
        });
    }

    private void changeToUpdateFragment(String cellId, String columnTitle, String rowTitle) {
        currentChosenCellId = cellId;
        viewModel.setUpdateCellId(cellId);
        BoardDetailUpdateFragment boardDetailUpdateFragment = (BoardDetailUpdateFragment) BoardDetailUpdateFragment.newInstance(cellId, columnTitle, rowTitle);
        if(boardDetailUpdateFragment != null) boardDetailUpdateFragment.setDone(isDone);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(activityBinding.fragmentContainer.getId(), boardDetailUpdateFragment, "BOARD_DETAIL_UPDATE_FRAGMENT")
                .commit();
    }

    private void changeToColumnFragment() {
        // "randombullshitgo" should be synced, should not change
        currentChosenCellId = "randombullshitgo";
        changeAllButtonBackgroundToDefault();
        DrawableCompat.setTint(activityBinding.btnShowColumns.getBackground(), ContextCompat.getColor(BoardItemDetailActivity.this, R.color.primary_btn_color));
        int colorTint = ContextCompat.getColor(BoardItemDetailActivity.this, R.color.white);
        activityBinding.btnShowColumns.setTextColor(colorTint);
        activityBinding.btnShowColumns.setCompoundDrawableTintList(ColorStateList.valueOf(colorTint));
        BoardDetailColumnFragment boardDetailColumnFragment = (BoardDetailColumnFragment) BoardDetailColumnFragment.newInstance();
        if(boardDetailColumnFragment != null) boardDetailColumnFragment.setDone(isDone);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(activityBinding.fragmentContainer.getId(), boardDetailColumnFragment, "BOARD_DETAIL_COLUMN_FRAGMENT")
                .commit();
    }

    @Override
    public void onUpdateItemClick(BoardUpdateItemModel itemModel, String columnTitle) {
        String cellId = itemModel.get_id();
        for (int i = 0; i < buttons.size(); i++) {
            if (Objects.equals(buttons.get(i).second, cellId)) {
                DrawableCompat.setTint(buttons.get(i).first.getBackground(), ContextCompat.getColor(BoardItemDetailActivity.this, R.color.primary_btn_color));
                changeToUpdateFragment(cellId, columnTitle, this.rowTitle);
            } else {
                DrawableCompat.setTint(buttons.get(i).first.getBackground(), ContextCompat.getColor(BoardItemDetailActivity.this, R.color.primary_btn_second_color));
            }
        }
    }

    private enum Role {CREATOR, ADMIN, MEMBER}
    private void loadViewWithSuitableRole(String creatorId, List<String> listAdminId)
    {
        Role role;
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        if(creatorId.equals(userId)) role = Role.CREATOR;
        else if(listAdminId.contains(userId)) role = Role.ADMIN;
        else role = Role.MEMBER;


        switch (role){
            case CREATOR:
                activityBinding.btnMoreOptions.setVisibility(View.VISIBLE);
                break;
            case ADMIN:
                activityBinding.btnMoreOptions.setVisibility(View.VISIBLE);
                break;
            case MEMBER:
                if(isAnOwnerOfRow(userId)) activityBinding.btnMoreOptions.setVisibility(View.VISIBLE);
                else activityBinding.btnMoreOptions.setVisibility(View.GONE);
                break;
        }
    }
    private boolean isAnOwnerOfRow(String id)
    {
        if(viewModel.getItemsLiveData().getValue() == null) return false;
        List<BoardBaseItemModel> cells = viewModel.getItemsLiveData().getValue().getCells();

        for (BoardBaseItemModel cell : cells) {
            if (cell.getCellType().equals("CellUser")) {
                BoardUserItemModel userItemModel = (BoardUserItemModel) cell;
                if (userItemModel != null) {
                    List<UserModel> users = userItemModel.getUsers();
                    for (UserModel user : users) {
                        if (user.getId().equals(id)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}