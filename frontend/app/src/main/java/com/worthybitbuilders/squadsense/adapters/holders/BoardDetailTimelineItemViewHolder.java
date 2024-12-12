package com.worthybitbuilders.squadsense.adapters.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardTimelineItemModel;

public class BoardDetailTimelineItemViewHolder extends AbstractViewHolder {
    private TextView tvContent;
    private TextView tvTitleColumn;
    private TimelineItemClickHandlers handlers;
    private ImageView deadlineClock;
    public BoardDetailTimelineItemViewHolder(@NonNull View itemView, TimelineItemClickHandlers handlers) {
        super(itemView);
        this.tvContent = itemView.findViewById(R.id.tvDateItemContent);
        this.tvTitleColumn = itemView.findViewById(R.id.tvColumnTitle);
        this.deadlineClock = itemView.findViewById(R.id.deadlineClock);
        this.handlers = handlers;
    }

    public void setItemModel(BoardTimelineItemModel itemModel, String columnTitle, int columnPos, int deadlineColumnIndex) {
        this.tvTitleColumn.setText(columnTitle);
        this.tvContent.setText(itemModel.getContent());
        this.tvContent.setOnClickListener((view) -> handlers.OnTimelineItemClick(itemModel, columnTitle, columnPos));
        if (columnPos == deadlineColumnIndex) this.deadlineClock.setVisibility(View.VISIBLE);
        else this.deadlineClock.setVisibility(View.GONE);
    }

    public interface TimelineItemClickHandlers {
        public void OnTimelineItemClick(BoardTimelineItemModel itemModel, String columnTitle, int columnPos);
    }
}
