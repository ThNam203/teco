package com.worthybitbuilders.squadsense.adapters.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardMapItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTextItemModel;

public class BoardDetailMapItemViewHolder extends AbstractViewHolder {
    private final TextView tvContent;
    private final TextView tvTitleColumn;
    private MapItemClickHandlers handlers;

    public BoardDetailMapItemViewHolder(@NonNull View itemView, MapItemClickHandlers handlers) {
        super(itemView);
        this.tvContent = itemView.findViewById(R.id.tvMapItemContent);
        this.tvTitleColumn = itemView.findViewById(R.id.tvColumnTitle);
        this.handlers = handlers;
    }

    public void setItemModel(@NonNull BoardMapItemModel itemModel, String columnTitle, int columnPos) {
        this.tvContent.setText(itemModel.getContent());
        this.tvTitleColumn.setText(columnTitle);
        this.tvContent.setOnClickListener((view -> handlers.onMapItemClick(itemModel, columnTitle, columnPos)));
    }

    public interface MapItemClickHandlers {
        void onMapItemClick(BoardMapItemModel itemModel, String columnTitle, int columnPos);
    }
}
