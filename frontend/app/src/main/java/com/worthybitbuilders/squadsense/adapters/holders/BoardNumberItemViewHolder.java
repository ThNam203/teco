package com.worthybitbuilders.squadsense.adapters.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardNumberItemModel;

public class BoardNumberItemViewHolder extends AbstractViewHolder {
    NumberItemClickHandlers handlers;
    TextView tvContent;
    public BoardNumberItemViewHolder(@NonNull View itemView, NumberItemClickHandlers handlers) {
        super(itemView);
        this.tvContent = itemView.findViewById(R.id.tvContentNumberItem);
        this.handlers = handlers;
    }

    public void setItemModel(BoardNumberItemModel itemModel, String columnTitle, int columnPos, int rowPos) {
        itemView.setOnClickListener((view) -> handlers.onNumberItemClick(itemModel, columnTitle, columnPos, rowPos));
        this.tvContent.setText(itemModel.getContent());
    }

    public interface NumberItemClickHandlers {
        void onNumberItemClick(BoardNumberItemModel itemModel, String columnTitle, int columnPos, int rowPos);
    }
}
