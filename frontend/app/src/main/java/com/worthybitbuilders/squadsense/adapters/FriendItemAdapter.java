package com.worthybitbuilders.squadsense.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.UserModel;

import java.util.List;

public class FriendItemAdapter extends RecyclerView.Adapter {
    private final List<UserModel> friendList;
    private OnActionCallback callback;

    public interface OnActionCallback {
        void OnClick(int position);
    }

    public FriendItemAdapter(List<UserModel> friendList) {
        this.friendList = friendList;
    }

    public void setOnClickListener(OnActionCallback callback) {
        this.callback = callback;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_friend, parent, false);
            return new FriendItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserModel friend= (UserModel) friendList.get(position);
        ((FriendItemAdapter.FriendItemHolder) holder).bind(friend, position);
    }


    @Override
    public int getItemCount() {
        return friendList.size();
    }

    private class FriendItemHolder extends RecyclerView.ViewHolder {
        TextView tvFriendName;
        ImageView friendAvatar;
        FriendItemHolder(View itemView) {
            super(itemView);
            tvFriendName = itemView.findViewById(R.id.friend_name);
            friendAvatar = itemView.findViewById(R.id.friend_avatar);
        }

        void bind(UserModel friend, int position) {
            tvFriendName.setText(friend.getName());

            Glide.with(itemView.getContext())
                    .load(friend.getProfileImagePath())
                    .placeholder(R.drawable.ic_user)
                    .into(friendAvatar);

            itemView.setOnClickListener(view -> callback.OnClick(position));
        }
    }
}