package com.worthybitbuilders.squadsense.adapters.holders;

import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardUpdateItemModel;

public class BoardUpdateItemViewHolder extends AbstractViewHolder {
    UpdateItemClickHandlers handlers;
    ImageButton btnEnterUpdate;
    public BoardUpdateItemViewHolder(@NonNull View itemView, UpdateItemClickHandlers handlers) {
        super(itemView);
        this.handlers = handlers;
        this.btnEnterUpdate = itemView.findViewById(com.worthybitbuilders.squadsense.R.id.btnEnterUpdate);
    }

    public void setItemModel(int rowPosition, String rowTitle) {
        btnEnterUpdate.setOnClickListener((view) -> handlers.onUpdateItemClick(rowPosition, rowTitle));
    }

    public interface UpdateItemClickHandlers {
        void onUpdateItemClick(int rowPosition, String rowTitle);
    }
}
