package com.worthybitbuilders.squadsense.adapters.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardDateItemModel;

public class BoardDetailDateItemViewHolder extends AbstractViewHolder {
    private TextView tvContent;
    private TextView tvTitleColumn;
    private DateItemClickHandlers handlers;
    private ImageView deadlineClock;
    public BoardDetailDateItemViewHolder(@NonNull View itemView, DateItemClickHandlers handlers) {
        super(itemView);
        this.tvContent = itemView.findViewById(R.id.tvDateItemContent);
        this.tvTitleColumn = itemView.findViewById(R.id.tvColumnTitle);
        this.deadlineClock = itemView.findViewById(R.id.deadlineClock);
        this.handlers = handlers;
    }

    public void setItemModel(BoardDateItemModel itemModel, String columnTitle, int columnPos, int deadlineColumnIndex) {
        this.tvContent.setText(itemModel.getContent());
        this.tvTitleColumn.setText(columnTitle);
        this.tvContent.setOnClickListener((view) -> handlers.OnDateItemClick(itemModel, columnTitle, columnPos));
        if (columnPos == deadlineColumnIndex) this.deadlineClock.setVisibility(View.VISIBLE);
        else this.deadlineClock.setVisibility(View.GONE);
    }

    public interface DateItemClickHandlers {
        public void OnDateItemClick(BoardDateItemModel itemModel, String columnTitle, int columnPos);
    }
}
