package com.worthybitbuilders.squadsense.adapters.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardTimelineItemModel;

public class BoardTimelineItemViewHolder extends AbstractViewHolder {
    private TextView tvContent;
    private TimelineItemClickHandlers handlers;
    private ImageView deadlineClock;
    public BoardTimelineItemViewHolder(@NonNull View itemView, TimelineItemClickHandlers handlers) {
        super(itemView);
        this.tvContent = itemView.findViewById(R.id.tvDateItemContent);
        this.deadlineClock = itemView.findViewById(R.id.deadlineClock);
        this.handlers = handlers;
    }

    public void setItemModel(BoardTimelineItemModel itemModel, String columnTitle, int columnPos, int rowPos, int deadlineColumnIndex) {
        tvContent.setText(itemModel.getContent());
        itemView.setOnClickListener((view) -> handlers.OnTimelineItemClick(itemModel, columnTitle, columnPos, rowPos));
        if (columnPos == deadlineColumnIndex) deadlineClock.setVisibility(View.VISIBLE);
        else deadlineClock.setVisibility(View.GONE);
    }

    public interface TimelineItemClickHandlers {
        public void OnTimelineItemClick(BoardTimelineItemModel itemModel, String columnTitle, int columnPos, int rowPos);
    }
}
