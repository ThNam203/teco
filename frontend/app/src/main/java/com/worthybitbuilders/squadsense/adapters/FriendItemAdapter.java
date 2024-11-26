package com.worthybitbuilders.squadsense.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.viewmodels.FriendViewModel;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class FriendItemAdapter extends RecyclerView.Adapter {
    private final List<UserModel> friendList;
    private OnActionCallback callback;
    FriendViewModel friendViewModel;
    private boolean isShowingButtonMore;

    public interface OnActionCallback {
        void OnItemClick(int position);
        void OnMoreOptionsClick(int position);
    }

    public FriendItemAdapter(List<UserModel> friendList) {
        this.friendList = friendList;
        this.isShowingButtonMore = true;
        friendViewModel = new ViewModelProvider(() -> new ViewModelStore()).get(FriendViewModel.class);
    }

    public FriendItemAdapter(List<UserModel> friendList, boolean isShowingButtonMore) {
        this.friendList = friendList;
        friendViewModel = new ViewModelProvider(() -> new ViewModelStore()).get(FriendViewModel.class);
        this.isShowingButtonMore = isShowingButtonMore;
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
        TextView tvFriendName, tvFriendConnection;
        ImageView friendAvatar;
        ImageButton btnMore;
        FriendItemHolder(View itemView) {
            super(itemView);
            tvFriendName = itemView.findViewById(R.id.friend_name);
            tvFriendConnection = itemView.findViewById(R.id.friend_connection);
            friendAvatar = itemView.findViewById(R.id.friend_avatar);
            btnMore = itemView.findViewById(R.id.btn_more);
        }

        void bind(UserModel friend, int position) {
            tvFriendName.setText(friend.getName());

            Glide.with(itemView.getContext())
                    .load(friend.getProfileImagePath())
                    .placeholder(R.drawable.ic_user)
                    .into(friendAvatar);

            friendViewModel.getFriendById(friend.getId(), new FriendViewModel.getFriendCallback() {
                @Override
                public void onSuccess(List<UserModel> friends) {
                    List<UserModel> listFriend1 = friends;
                    String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
                    friendViewModel.getFriendById(userId, new FriendViewModel.getFriendCallback() {
                        @Override
                        public void onSuccess(List<UserModel> friends) {
                            List<UserModel> sameElements = new ArrayList<>(listFriend1); // Tạo một danh sách mới để giữ lại các phần tử chung
                            sameElements.retainAll(friends);
                            final int numSameElements = sameElements.size();
                            tvFriendConnection.setText(String.valueOf(numSameElements) + (numSameElements > 1 ? " connections" : " connection"));
                        }

                        @Override
                        public void onFailure(String message) {

                        }
                    });
                }

                @Override
                public void onFailure(String message) {

                }
            });

            itemView.setOnClickListener((view) -> callback.OnItemClick(position));
            if (isShowingButtonMore) btnMore.setOnClickListener(view -> callback.OnMoreOptionsClick(position));
            else btnMore.setVisibility(View.GONE);
        }
    }
}