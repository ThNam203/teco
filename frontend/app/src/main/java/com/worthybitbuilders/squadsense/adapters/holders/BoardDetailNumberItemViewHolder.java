package com.worthybitbuilders.squadsense.adapters.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardNumberItemModel;

public class BoardDetailNumberItemViewHolder extends AbstractViewHolder {
    private NumberItemClickHandlers handlers;
    private TextView tvTitleColumn;
    TextView tvContent;
    public BoardDetailNumberItemViewHolder(@NonNull View itemView, NumberItemClickHandlers handlers) {
        super(itemView);
        this.tvContent = itemView.findViewById(R.id.tvContentNumberItem);
        this.tvTitleColumn = itemView.findViewById(R.id.tvColumnTitle);
        this.handlers = handlers;
    }

    public void setItemModel(BoardNumberItemModel itemModel, String columnTitle, int columnPos) {
        this.tvTitleColumn.setText(columnTitle);
        this.tvContent.setOnClickListener((view) -> handlers.onNumberItemClick(itemModel, columnTitle, columnPos));
        this.tvContent.setText(itemModel.getContent());
    }

    public interface NumberItemClickHandlers {
        void onNumberItemClick(BoardNumberItemModel itemModel, String columnTitle, int columnPos);
    }
}
