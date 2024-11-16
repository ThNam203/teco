package com.worthybitbuilders.squadsense.adapters.holders;

import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardCheckboxItemModel;

public class BoardDetailCheckboxItemViewHolder extends AbstractViewHolder {
    private CheckBox cbCheckBox;
    private FrameLayout checkboxContainer;
    private TextView tvTitleColumn;
    private CheckboxItemClickHandlers handlers;

    public BoardDetailCheckboxItemViewHolder(@NonNull View itemView, CheckboxItemClickHandlers handlers) {
        super(itemView);
        this.handlers = handlers;
        this.tvTitleColumn = itemView.findViewById(R.id.tvColumnTitle);
        cbCheckBox = itemView.findViewById(R.id.checkBoxItemCheckbox);
        checkboxContainer = itemView.findViewById(R.id.checkBoxItemContainer);
    }

    public void setItemModel(BoardCheckboxItemModel itemModel, String columnTitle, int columnPos) {
        this.tvTitleColumn.setText(columnTitle);
        this.cbCheckBox.setChecked(itemModel.getChecked());
        this.cbCheckBox.setOnClickListener((view) -> handlers.onCheckboxItemClick(itemModel, columnPos));
    }

    public interface CheckboxItemClickHandlers {
        void onCheckboxItemClick(BoardCheckboxItemModel itemModel, int columnPos);
    }
}
