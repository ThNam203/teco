package com.worthybitbuilders.squadsense.adapters.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardDateItemModel;

import java.util.Date;

public class BoardDateItemViewHolder extends AbstractViewHolder {
    private TextView tvContent;
    private DateItemClickHandlers handlers;
    public BoardDateItemViewHolder(@NonNull View itemView, DateItemClickHandlers handlers) {
        super(itemView);
        this.tvContent = itemView.findViewById(R.id.tvDateItemContent);
        this.handlers = handlers;
    }

    public void setItemModel(BoardDateItemModel itemModel, String columnTitle, int columnPos, int rowPos) {
        tvContent.setText(itemModel.getContent());
        itemView.setOnClickListener((view) -> handlers.OnDateItemClick(itemModel, columnTitle, columnPos, rowPos));
    }

    public interface DateItemClickHandlers {
        public void OnDateItemClick(BoardDateItemModel itemModel, String columnTitle, int columnPos, int rowPos);
    }
}
