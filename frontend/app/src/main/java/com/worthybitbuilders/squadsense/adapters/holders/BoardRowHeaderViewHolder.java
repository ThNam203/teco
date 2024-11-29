package com.worthybitbuilders.squadsense.adapters.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardRowHeaderModel;

public class BoardRowHeaderViewHolder extends AbstractViewHolder {
    public ConstraintLayout container;
    public TextView headerTitle;
    public ImageView doneTick;

    public BoardRowHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        this.container = itemView.findViewById(R.id.boardRowHeaderContainer);
        this.headerTitle = itemView.findViewById(R.id.tvRowHeader);
        this.doneTick = itemView.findViewById(R.id.doneTick);
    }

    public void setRowHeaderModel (BoardRowHeaderModel rowHeaderModel) {
        headerTitle.setText(rowHeaderModel.getTitle());
        if (rowHeaderModel.isDone()) doneTick.setVisibility(View.VISIBLE);
        else doneTick.setVisibility(View.GONE);
    }
}
