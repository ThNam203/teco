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
    public ImageView userSecondButton;
    public TextView moreUsersHolder;
    UserItemClickHandler handlers;
    public BoardDetailUserItemViewHolder(@NonNull View itemView, UserItemClickHandler handlers) {
        super(itemView);
        this.userButton = itemView.findViewById(R.id.taskItemUser);
        this.tvTitleColumn = itemView.findViewById(R.id.tvColumnTitle);
        this.userSecondButton = itemView.findViewById(R.id.taskItemSecondUser);
        this.moreUsersHolder = itemView.findViewById(R.id.taskItemMoreUser);
        this.handlers = handlers;
    }

    public void setItemModel(BoardUserItemModel userItemModel, String columnTitle, Context context, int columnPosition) {
        this.tvTitleColumn.setText(columnTitle);
        userButton.setOnClickListener((view) -> handlers.onUserItemClick(userItemModel, columnTitle, columnPosition));
        if (userItemModel.getUsers() == null || userItemModel.getUsers().size() == 0) return;
        Glide.with(context).load(userItemModel.getUsers().get(0).getProfileImagePath()).placeholder(R.drawable.ic_user).into(userButton);
        if (userItemModel.getUsers().size() == 2) {
            userSecondButton.setVisibility(View.VISIBLE);
            Glide.with(context).load(userItemModel.getUsers().get(1).getProfileImagePath()).placeholder(R.drawable.ic_user).into(userSecondButton);
        }
        else if (userItemModel.getUsers().size() > 2) {
            moreUsersHolder.setVisibility(View.VISIBLE);
            moreUsersHolder.setText("+" + String.valueOf(userItemModel.getUsers().size() - 1));
        }
    }

    public interface UserItemClickHandler {
        void onUserItemClick(BoardUserItemModel userItemModel, String columnTitle, int columnPosition);
    }
}
