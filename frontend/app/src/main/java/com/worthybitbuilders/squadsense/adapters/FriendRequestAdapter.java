package com.worthybitbuilders.squadsense.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.Notification;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter{
    private static final int VIEW_TYPE_FRIEND_REQUEST = 0;
    private List<Notification> inboxFriendRequestList;
    private OnActionCallback callback;

    private UserViewModel userViewModel;

    public interface OnActionCallback {
        void OnAccept(int position);
        void OnDeny(int position);
    }

    public FriendRequestAdapter(List<Notification> inboxFriendRequestList) {
        this.inboxFriendRequestList = inboxFriendRequestList;
        userViewModel = new ViewModelProvider(new ViewModelStoreOwner() {
            @NonNull
            @Override
            public ViewModelStore getViewModelStore() {
                return new ViewModelStore();
            }
        }).get(UserViewModel.class);
    }

    public void setOnReplyListener(FriendRequestAdapter.OnActionCallback callback)
    {
        this.callback = callback;
    }

    @Override
    public int getItemViewType(int position) {
        Notification notification = (Notification) inboxFriendRequestList.get(position);
        if (notification.getNotificationType().equals("FriendRequest")) {
            return VIEW_TYPE_FRIEND_REQUEST;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_FRIEND_REQUEST) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_inbox_friend_request, parent, false);
            return new FriendRequestAdapter.FriendRequestInboxHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Notification notification = (Notification) inboxFriendRequestList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_FRIEND_REQUEST:
                ((FriendRequestAdapter.FriendRequestInboxHolder) holder).bind(notification, position);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return inboxFriendRequestList.size();
    }

    private class FriendRequestInboxHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvTimestamps;
        AppCompatButton btnAccept, btnDeny;
        ImageView userImage;

        FriendRequestInboxHolder(View itemView) {
            super(itemView);
            tvContent = (TextView) itemView.findViewById(R.id.content);
            tvTimestamps = (TextView) itemView.findViewById(R.id.timestamps);
            userImage = (ImageView) itemView.findViewById(R.id.user_image);
            btnAccept = (AppCompatButton) itemView.findViewById(R.id.btn_accept);
            btnDeny = (AppCompatButton) itemView.findViewById(R.id.btn_deny);
        }

        void bind(Notification notification, int position) {
            String content = notification.getTitle() + " " + notification.getContent();
            SpannableString spannableString = new SpannableString(content);
            int numberOfCharsToBold = notification.getTitle().length(); // Ví dụ: in đậm 4 chữ đầu
            if (numberOfCharsToBold <= content.length()) {
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, numberOfCharsToBold, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            tvContent.setText(spannableString);
            tvTimestamps.setText(notification.getCustomTimeCreated());

            userViewModel.getUserById(notification.getSenderId(), new UserViewModel.UserCallback() {
                @Override
                public void onSuccess(UserModel user) {
                    String imagePath = user.getProfileImagePath();
                    if(imagePath != null && !imagePath.isEmpty()){
                        Glide.with(itemView.getContext())
                                .load(imagePath)
                                .placeholder(R.drawable.ic_user)
                                .into(userImage);
                    }
                }

                @Override
                public void onFailure(String message) {

                }
            });

            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.OnAccept(position);
                }
            });
            btnDeny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.OnDeny(position);
                }
            });
        }
    }
}
