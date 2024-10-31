package com.worthybitbuilders.squadsense.adapters.holders;

import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.models.board_models.BoardUpdateItemModel;

public class BoardUpdateItemViewHolder extends AbstractViewHolder {
    UpdateItemClickHandlers handlers;
    ImageButton btnEnterUpdate;
    public BoardUpdateItemViewHolder(@NonNull View itemView, UpdateItemClickHandlers handlers) {
        super(itemView);
        this.handlers = handlers;
        this.btnEnterUpdate = itemView.findViewById(com.worthybitbuilders.squadsense.R.id.btnEnterUpdate);
    }

    public void setItemModel(BoardUpdateItemModel itemModel, int rowPosition, String rowTitle, String columnTitle) {
        btnEnterUpdate.setOnClickListener((view) -> handlers.onUpdateItemClick(itemModel, rowPosition, rowTitle, columnTitle));
    }

    public interface UpdateItemClickHandlers {
        void onUpdateItemClick(BoardUpdateItemModel itemModel, int rowPosition, String rowTitle, String columnTitle);
    }
}
