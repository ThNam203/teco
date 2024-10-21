package com.worthybitbuilders.squadsense.adapters.holders;

import android.view.View;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.models.board_models.BoardUpdateItemModel;

public class BoardUpdateItemViewHolder extends AbstractViewHolder {
    UpdateItemClickHandlers handlers;
    public BoardUpdateItemViewHolder(@NonNull View itemView, UpdateItemClickHandlers handlers) {
        super(itemView);
        this.handlers = handlers;
    }

    public void setItemModel(BoardUpdateItemModel itemModel, String columnTitle) {
        itemView.setOnClickListener((view) -> handlers.onUpdateItemClick(itemModel, columnTitle));
    }

    public interface UpdateItemClickHandlers {
        void onUpdateItemClick(BoardUpdateItemModel itemModel, String columnTitle);
    }
}
