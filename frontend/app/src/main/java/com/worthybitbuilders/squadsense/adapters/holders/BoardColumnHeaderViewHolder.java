package com.worthybitbuilders.squadsense.adapters.holders;

import android.view.View;
import android.widget.TextView;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardColumnHeaderModel;

public class BoardColumnHeaderViewHolder extends AbstractViewHolder {
    private final TextView headerTitle;

    public BoardColumnHeaderViewHolder(View itemView) {
        super(itemView);
        headerTitle = itemView.findViewById(R.id.boardColumnHeader);
    }

    public void setColumnHeaderModel(BoardColumnHeaderModel columnHeaderModel) {
        headerTitle.setText(columnHeaderModel.getTitle());
        // It is necessary to remeasure itself.
//        headerTitle.requestLayout();
    }
}