package com.worthybitbuilders.squadsense.adapters.holders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardUpdateItemModel;

public class BoardDetailUpdateItemViewHolder extends RecyclerView.ViewHolder {
    UpdateItemClickHandlers handlers;
    ImageButton btnEnterUpdate;
    private final TextView tvTitleColumn;
    public BoardDetailUpdateItemViewHolder(@NonNull View itemView, UpdateItemClickHandlers handlers) {
        super(itemView);
        this.handlers = handlers;
        this.tvTitleColumn = itemView.findViewById(R.id.tvColumnTitle);
        this.btnEnterUpdate = itemView.findViewById(R.id.btnEnterUpdate);
    }

    public void setItemModel(BoardUpdateItemModel itemModel, String columnTitle) {
        this.tvTitleColumn.setText(columnTitle);
        btnEnterUpdate.setOnClickListener((view) -> handlers.onUpdateItemClick(itemModel, columnTitle));
    }

    public interface UpdateItemClickHandlers {
        void onUpdateItemClick(BoardUpdateItemModel itemModel, String columnTitle);
    }
}
