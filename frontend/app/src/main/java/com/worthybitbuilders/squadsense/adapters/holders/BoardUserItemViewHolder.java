package com.worthybitbuilders.squadsense.adapters.holders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;

public class BoardUserItemViewHolder extends AbstractViewHolder {
    public ImageView userButton;
    UserItemClickHandler handlers;
    public BoardUserItemViewHolder(@NonNull View itemView, UserItemClickHandler handlers) {
        super(itemView);
        userButton = itemView.findViewById(R.id.taskItemUser);
        this.handlers = handlers;
    }

    public void setItemModel(BoardUserItemModel userItemModel, Context context) {
        if (!userItemModel.getUserImagePath().isEmpty() && userItemModel.getUserImagePath() != null)
            Glide.with(context).load(userItemModel.getUserImagePath()).into(userButton);
        itemView.setOnClickListener((view) -> handlers.onUserItemClick(userItemModel));
    }

    public interface UserItemClickHandler {
        void onUserItemClick(BoardUserItemModel userItemModel);
    }
}
