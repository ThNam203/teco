package com.worthybitbuilders.squadsense.adapters.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardTextItemModel;

public class BoardDetailTextItemViewHolder extends AbstractViewHolder {
    private final TextView tvContent;
    private final TextView tvTitleColumn;
    private TextItemClickHandlers handlers;

    public BoardDetailTextItemViewHolder(@NonNull View itemView, TextItemClickHandlers handlers) {
        super(itemView);
        this.tvContent = itemView.findViewById(R.id.tvContentTextItem);
        this.tvTitleColumn = itemView.findViewById(R.id.tvColumnTitle);
        this.handlers = handlers;
    }

    public void setItemModel(@NonNull BoardTextItemModel itemModel, String columnTitle, int columnPos) {
        this.tvContent.setText(itemModel.getContent());
        this.tvTitleColumn.setText(columnTitle);
        this.tvContent.setOnClickListener((view -> handlers.onTextItemClick(itemModel, columnTitle, columnPos)));
    }

    public interface TextItemClickHandlers {
        void onTextItemClick(BoardTextItemModel itemModel, String columnTitle, int columnPos);
    }
}
