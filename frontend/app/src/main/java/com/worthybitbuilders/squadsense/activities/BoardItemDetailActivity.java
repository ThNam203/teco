package com.worthybitbuilders.squadsense.activities;

import android.app.Dialog;
import android.content.Intent;
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
import androidx.core.graphics.drawable.DrawableCompat;
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
import com.worthybitbuilders.squadsense.models.board_models.BoardUpdateItemModel;
import com.worthybitbuilders.squadsense.utils.DialogUtil;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.BoardDetailItemViewModel;

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
    private String currentChosenCellId = null;
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
        String projectTitle = intent.getStringExtra("projectTitle");
        String boardTitle = intent.getStringExtra("boardTitle");

        // updateCellId is only for when user tap the update item
        // which is because there could be more than 1 update item
        String updateCellId = intent.getStringExtra("updateCellId");
        String updateCellTitle = intent.getStringExtra("updateCellTitle");
        int rowPosition = intent.getIntExtra("rowPosition", -1);

        activityBinding.itemTitle.setText(rowTitle);

        BoardItemDetailViewModelFactory viewModelFactory = new BoardItemDetailViewModelFactory(rowPosition, projectId, boardId, projectTitle, boardTitle, rowTitle);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(BoardDetailItemViewModel.class);

        // check if user press "update" column or "row header"
        if (rowPosition == -1 || projectId.isEmpty() || boardId.isEmpty()) {
            ToastUtils.showToastError(this, "Something went wrong, please try again", Toast.LENGTH_LONG);
            finish();
        }

        Dialog loadingDialog = DialogUtil.GetLoadingDialog(this);
        loadingDialog.show();

        viewModel.getDataFromRemote(new BoardDetailItemViewModel.ApiCallHandler() {
            @Override
            public void onSuccess() {
                BoardDetailItemModel data = viewModel.getItemsLiveData().getValue();
                if (data == null) return;
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
                        DrawableCompat.setTint(updateButton.getBackground(), Color.parseColor("#0073ea"));
                        changeToUpdateFragment(updateCellId, data.getColumnTitles().get(finalI));
                    }

                    updateButton.setOnClickListener(view -> {
                        if (Objects.equals(currentChosenCellId, cellId)) return;
                        changeAllButtonBackgroundToDefault();
                        DrawableCompat.setTint(updateButton.getBackground(), Color.parseColor("#0073ea"));
                        changeToUpdateFragment(cellId, data.getColumnTitles().get(finalI));
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

        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(this.activityBinding.btnMoreOptions, 0, 0);
    }

    private void showConfirmDelete() {
        final Dialog confirmDialog = new Dialog(this);
        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ConfirmDeleteSecondaryBinding binding = ConfirmDeleteSecondaryBinding.inflate(getLayoutInflater());
        confirmDialog.setContentView(binding.getRoot());

        binding.tvTitle.setText(String.format(Locale.US, "Delete row \"%s\"?", viewModel.getRowTitle()));
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

        binding.etTextItem.setText(viewModel.getRowTitle());
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
                        activityBinding.itemTitle.setText(viewModel.getRowTitle());
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
        buttons.forEach(pair -> DrawableCompat.setTint(pair.first.getBackground(), Color.parseColor("#2b2b2b")));
    }

    private void changeToUpdateFragment(String cellId, String columnTitle) {
        currentChosenCellId = cellId;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(activityBinding.fragmentContainer.getId(), BoardDetailUpdateFragment.newInstance(cellId, columnTitle))
                .commit();
    }

    private void changeToColumnFragment() {
        // "randombullshitgo" should be synced, should not change
        currentChosenCellId = "randombullshitgo";
        changeAllButtonBackgroundToDefault();
        DrawableCompat.setTint(activityBinding.btnShowColumns.getBackground(), Color.parseColor("#0073ea"));
        getSupportFragmentManager()
                .beginTransaction()
                .replace(activityBinding.fragmentContainer.getId(), BoardDetailColumnFragment.newInstance())
                .commit();
    }

    @Override
    public void onUpdateItemClick(BoardUpdateItemModel itemModel, String columnTitle) {
        String cellId = itemModel.get_id();
        for (int i = 0; i < buttons.size(); i++) {
            if (Objects.equals(buttons.get(i).second, cellId)) {
                DrawableCompat.setTint(buttons.get(i).first.getBackground(), Color.parseColor("#0073ea"));
                changeToUpdateFragment(cellId, columnTitle);
            } else {
                DrawableCompat.setTint(buttons.get(i).first.getBackground(), Color.parseColor("#2b2b2b"));
            }
        }
    }
}