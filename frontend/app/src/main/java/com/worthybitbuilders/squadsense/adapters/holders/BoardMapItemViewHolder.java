package com.worthybitbuilders.squadsense.adapters.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardMapItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTextItemModel;

public class BoardMapItemViewHolder extends AbstractViewHolder {
    private final TextView tvContent;
    private final MapItemClickHandlers handlers;

    public BoardMapItemViewHolder(@NonNull View itemView, MapItemClickHandlers handlers) {
        super(itemView);
        this.tvContent = itemView.findViewById(R.id.tvMapItemContent);
        this.handlers = handlers;
    }

    public void setItemModel(@NonNull BoardMapItemModel itemModel, String columnTitle, int columnPos, int rowPos) {
        this.tvContent.setText(itemModel.getContent());
        this.itemView.setOnClickListener((view -> handlers.OnMapItemClick(itemModel, columnTitle, columnPos, rowPos)));
    }

    public interface MapItemClickHandlers {
        void OnMapItemClick(BoardMapItemModel itemModel, String columnTitle, int columnPos, int rowPos);
    }
}
