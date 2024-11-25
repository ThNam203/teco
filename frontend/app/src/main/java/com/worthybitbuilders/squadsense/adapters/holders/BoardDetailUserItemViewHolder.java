package com.worthybitbuilders.squadsense.adapters.holders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;

public class BoardDetailUserItemViewHolder extends AbstractViewHolder {
    private RelativeLayout layoutUserButton;
    private ImageView userButton;
    private TextView tvTitleColumn;
    public ImageView userSecondButton;
    public TextView moreUsersHolder;
    UserItemClickHandler handlers;
    public BoardDetailUserItemViewHolder(@NonNull View itemView, UserItemClickHandler handlers) {
        super(itemView);
        this.layoutUserButton = itemView.findViewById(R.id.layoutTaskItemUser);
        this.userButton = itemView.findViewById(R.id.taskItemUser);
        this.tvTitleColumn = itemView.findViewById(R.id.tvColumnTitle);
        this.userSecondButton = itemView.findViewById(R.id.taskItemSecondUser);
        this.moreUsersHolder = itemView.findViewById(R.id.taskItemMoreUser);
        this.handlers = handlers;
    }

    public void setItemModel(BoardUserItemModel userItemModel, String columnTitle, Context context, int columnPosition) {
        this.tvTitleColumn.setText(columnTitle);
        layoutUserButton.setOnClickListener((view) -> handlers.onUserItemClick(userItemModel, columnTitle, columnPosition));
        if (userItemModel.getUsers() == null || userItemModel.getUsers().size() == 0) {
            userButton.setVisibility(View.VISIBLE);
            userSecondButton.setVisibility(View.GONE);
            moreUsersHolder.setVisibility(View.GONE);
            Glide.with(context).load(R.drawable.ic_person).into(userButton);
        }
        else if(userItemModel.getUsers().size() == 1)
        {
            userButton.setVisibility(View.VISIBLE);
            userSecondButton.setVisibility(View.GONE);
            moreUsersHolder.setVisibility(View.GONE);
            Glide.with(context).load(userItemModel.getUsers().get(0).getProfileImagePath()).placeholder(R.drawable.ic_user).into(userButton);
        }
        else if (userItemModel.getUsers().size() == 2) {
            userButton.setVisibility(View.VISIBLE);
            userSecondButton.setVisibility(View.VISIBLE);
            moreUsersHolder.setVisibility(View.GONE);
            Glide.with(context).load(userItemModel.getUsers().get(0).getProfileImagePath()).placeholder(R.drawable.ic_user).into(userButton);
            Glide.with(context).load(userItemModel.getUsers().get(1).getProfileImagePath()).placeholder(R.drawable.ic_user).into(userSecondButton);
        }
        else if (userItemModel.getUsers().size() > 2) {
            userButton.setVisibility(View.VISIBLE);
            userSecondButton.setVisibility(View.GONE);
            moreUsersHolder.setVisibility(View.VISIBLE);
            moreUsersHolder.setText("+" + String.valueOf(userItemModel.getUsers().size() - 1));
            Glide.with(context).load(userItemModel.getUsers().get(0).getProfileImagePath()).placeholder(R.drawable.ic_user).into(userButton);
        }
    }

    public interface UserItemClickHandler {
        void onUserItemClick(BoardUserItemModel userItemModel, String columnTitle, int columnPosition);
    }
}
