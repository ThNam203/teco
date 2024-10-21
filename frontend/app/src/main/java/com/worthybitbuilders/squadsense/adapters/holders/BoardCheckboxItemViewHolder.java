package com.worthybitbuilders.squadsense.adapters.holders;

import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardCheckboxItemModel;

public class BoardCheckboxItemViewHolder extends AbstractViewHolder {
    private CheckBox cbCheckBox;
    private CheckboxItemClickHandlers handlers;

    public BoardCheckboxItemViewHolder(@NonNull View itemView, CheckboxItemClickHandlers handlers) {
        super(itemView);
        this.handlers = handlers;
        cbCheckBox = itemView.findViewById(R.id.checkBoxItemCheckbox);
    }

    public void setItemModel(BoardCheckboxItemModel itemModel, int columnPos, int rowPos) {
        cbCheckBox.setChecked(itemModel.getChecked());
        itemView.setOnClickListener((view) -> handlers.onCheckboxItemClick(itemModel, columnPos, rowPos));
    }

    public interface CheckboxItemClickHandlers {
        void onCheckboxItemClick(BoardCheckboxItemModel itemModel, int columnPos, int rowPos);
    }
}
