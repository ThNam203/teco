package com.worthybitbuilders.squadsense.adapters.holders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardRowHeaderModel;

public class BoardRowHeaderViewHolder extends AbstractViewHolder {
    public LinearLayout container;
    public TextView headerTitle;

    public BoardRowHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        this.container = itemView.findViewById(R.id.boardRowHeaderContainer);
        this.headerTitle = itemView.findViewById(R.id.tvRowHeader);
    }

    public void setRowHeaderModel (BoardRowHeaderModel rowHeaderModel) {
        headerTitle.setText(rowHeaderModel.getTitle());
    }
}
