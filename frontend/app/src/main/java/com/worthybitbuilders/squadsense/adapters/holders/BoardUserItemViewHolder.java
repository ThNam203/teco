package com.worthybitbuilders.squadsense.adapters.holders;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.google.android.material.imageview.ShapeableImageView;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;

public class BoardUserItemViewHolder extends AbstractViewHolder {
    public ShapeableImageView userButton;
    public ShapeableImageView userSecondButton;
    public TextView moreUsersHolder;
    UserItemClickHandler handlers;
    public BoardUserItemViewHolder(@NonNull View itemView, UserItemClickHandler handlers) {
        super(itemView);
        userButton = itemView.findViewById(R.id.taskItemUser);
        userSecondButton = itemView.findViewById(R.id.taskItemSecondUser);
        moreUsersHolder = itemView.findViewById(R.id.taskItemMoreUser);
        this.handlers = handlers;
    }

    public void setItemModel(BoardUserItemModel userItemModel, Context context, String columnTitle, int columnPos, int rowPos) {
        itemView.setOnClickListener((view) -> handlers.onUserItemClick(userItemModel, columnTitle, columnPos, rowPos));
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
        void onUserItemClick(BoardUserItemModel userItemModel, String columnTitle, int columnPos, int rowPos);
    }
}
