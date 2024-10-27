package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ActivityBoardItemDetailBinding;
import com.worthybitbuilders.squadsense.factory.BoardItemDetailViewModelFactory;
import com.worthybitbuilders.squadsense.fragments.BoardDetailColumnFragment;
import com.worthybitbuilders.squadsense.fragments.BoardDetailUpdateFragment;
import com.worthybitbuilders.squadsense.models.BoardDetailItemModel;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.viewmodels.BoardDetailItemViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BoardItemDetailActivity extends AppCompatActivity {
    final private List<Button> buttons = new ArrayList<>();
    private ActivityBoardItemDetailBinding binding;
    private BoardDetailItemViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBoardItemDetailBinding.inflate(getLayoutInflater());
        Objects.requireNonNull(getSupportActionBar()).hide();

        buttons.add(binding.btnShowColumns);
        binding.btnBack.setOnClickListener(view -> this.onBackPressed());

        Intent intent = getIntent();
        boolean isFromUpdateColumn = intent.getBooleanExtra("isFromUpdateColumn", false);
        String projectId = intent.getStringExtra("projectId");
        String boardId = intent.getStringExtra("boardId");
        String rowTitle = intent.getStringExtra("rowTitle");

        // updateCellId is only for when user tap the update item
        // which is because there could be more than 1 update item
        String updateCellId = intent.getStringExtra("updateCellId");
        int rowPosition = intent.getIntExtra("rowPosition", -1);

        binding.itemTitle.setText(rowTitle);

        BoardItemDetailViewModelFactory viewModelFactory = new BoardItemDetailViewModelFactory(rowPosition);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(BoardDetailItemViewModel.class);

        // check if user press "update" column or "row header"
        if (rowPosition == -1 || projectId.isEmpty() || boardId.isEmpty()) {
            Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_LONG).show();
            finish();
        }

        Dialog loadingDialog = DialogUtils.GetLoadingDialog(this);
        loadingDialog.show();

        viewModel.getDataFromRemote(projectId, boardId, rowPosition, new BoardDetailItemViewModel.GetDataHandlers() {
            @Override
            public void onSuccess() {
                BoardDetailItemModel data = viewModel.getItemsLiveData().getValue();
                if (data == null) return;
                for (int i = 0; i < data.getCells().size(); i++) {
                    if (!Objects.equals(data.getCells().get(i).getCellType(), "CellUpdate")) continue;
                    View updateButton = LayoutInflater.from(BoardItemDetailActivity.this).inflate(R.layout.board_detail_item_update_button_view, null, false);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(16, 0, 16, 0);
                    updateButton.setLayoutParams(layoutParams);
                    String cellId = data.getCells().get(i).get_id();
                    updateButton.setOnClickListener(view -> {
                        changeAllButtonBackgroundToDefault();
                        DrawableCompat.setTint(updateButton.getBackground(), Color.parseColor("#0073ea"));
                        changeToUpdateFragment(cellId);
                    });
                    buttons.add((Button)updateButton);
                    binding.buttonsContainer.addView(updateButton);
                }

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(BoardItemDetailActivity.this, "Unable to get data, please try again", Toast.LENGTH_LONG).show();
                loadingDialog.dismiss();
            }
        });

        if (isFromUpdateColumn) changeToUpdateFragment(updateCellId);
        else changeToColumnFragment(projectId, boardId);

        binding.btnShowColumns.setOnClickListener(view -> changeToColumnFragment(projectId, boardId));
        setContentView(binding.getRoot());
    }

    private void changeAllButtonBackgroundToDefault() {
        buttons.forEach(button -> DrawableCompat.setTint(button.getBackground(), Color.parseColor("#2b2b2b")));
    }

    private void changeToUpdateFragment(String cellId) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.fragmentContainer.getId(), BoardDetailUpdateFragment.newInstance(cellId))
                .commit();
    }

    private void changeToColumnFragment(String projectId, String boardId) {
        changeAllButtonBackgroundToDefault();
        DrawableCompat.setTint(binding.btnShowColumns.getBackground(), Color.parseColor("#0073ea"));
        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.fragmentContainer.getId(), new BoardDetailColumnFragment(viewModel, projectId, boardId))
                .commit();
    }
}