package com.worthybitbuilders.squadsense.adapters.holders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;

public class BoardDetailUserItemViewHolder extends AbstractViewHolder {
    private ImageView userButton;
    private TextView tvTitleColumn;
    UserItemClickHandler handlers;
    public BoardDetailUserItemViewHolder(@NonNull View itemView, UserItemClickHandler handlers) {
        super(itemView);
        this.userButton = itemView.findViewById(R.id.taskItemUser);
        this.tvTitleColumn = itemView.findViewById(R.id.tvColumnTitle);
        this.handlers = handlers;
    }

    public void setItemModel(BoardUserItemModel userItemModel, String columnTitle, Context context, int columnPosition) {
        this.tvTitleColumn.setText(columnTitle);
        if (!userItemModel.getUserImagePath().isEmpty() && userItemModel.getUserImagePath() != null)
            Glide.with(context).load(userItemModel.getUserImagePath()).into(userButton);
        userButton.setOnClickListener((view) -> handlers.onUserItemClick(userItemModel, columnPosition));
    }

    public interface UserItemClickHandler {
        void onUserItemClick(BoardUserItemModel userItemModel, int columnPosition);
    }
}
