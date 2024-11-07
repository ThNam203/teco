package com.worthybitbuilders.squadsense.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.Notification;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter{
    private static final int VIEW_TYPE_FRIEND_REQUEST = 0;
    private List<Notification> inboxFriendRequestList;
    private FriendRequestAdapter.OnActionCallback callback;

    public interface OnActionCallback {
        void OnAccept(int position);
        void OnDeny(int position);
    }

    public FriendRequestAdapter(List<Notification> inboxFriendRequestList) {
        this.inboxFriendRequestList = inboxFriendRequestList;
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
        TextView tvTitle, tvContent, tvTimestamps;
        AppCompatButton btnAccept, btnDeny;

        FriendRequestInboxHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.title);
            tvContent = (TextView) itemView.findViewById(R.id.content);
            tvTimestamps = (TextView) itemView.findViewById(R.id.timestamps);
            btnAccept = (AppCompatButton) itemView.findViewById(R.id.btn_accept);
            btnDeny = (AppCompatButton) itemView.findViewById(R.id.btn_deny);
        }

        void bind(Notification notification, int position) {
            tvTitle.setText(notification.getTitle());
            tvContent.setText(notification.getContent());
            tvTimestamps.setText(notification.getTimeCreated());

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
