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
    private static final int VIEW_TYPE_DEFAULT_FRIEND = 0;
    private Context context;
    private List<UserModel> friendList;
    private OnActionCallback callback;

    public interface OnActionCallback {
        void OnClick(int position);
    }

    public FriendItemAdapter(Context context, List<UserModel> friendList) {
        this.context = context;
        this.friendList = friendList;
    }

    public void setOnClickListener(OnActionCallback callback)
    {
        this.callback = callback;
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DEFAULT_FRIEND;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_DEFAULT_FRIEND) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_friend, parent, false);
            return new FriendItemHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserModel friend= (UserModel) friendList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_DEFAULT_FRIEND:
                ((FriendItemAdapter.FriendItemHolder) holder).bind(friend, position);
                break;
        }
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
            String imagePath = friend.getProfileImagePath();

            if(imagePath != null && !imagePath.isEmpty())
            {
                String publicProfileImageURL = String.format("https://squadsense.s3.ap-southeast-1.amazonaws.com/%s", imagePath);
                Glide.with(itemView.getContext())
                        .load(publicProfileImageURL)
                        .into(friendAvatar);
            }

            itemView.setOnClickListener(view -> callback.OnClick(position));
        }
    }
}