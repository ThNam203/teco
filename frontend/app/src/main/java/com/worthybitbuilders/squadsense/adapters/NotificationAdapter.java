package com.worthybitbuilders.squadsense.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.models.Notification;
import com.worthybitbuilders.squadsense.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_FRIEND_REQUEST = 0;
    private static final int VIEW_TYPE_NEW_MESSAGE = 1;
    private Context context;
    private List<Notification> notificationList;
    private OnActionCallback callback;

    public interface OnActionCallback {
        void OnAccept(int position);
        void OnDeny(int position);

        void OnShowingOption(int position);

    }

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    public void setOnReplyListener(OnActionCallback callback)
    {
        this.callback = callback;
    }

    @Override
    public int getItemViewType(int position) {
        Notification notification = (Notification) notificationList.get(position);
        if (notification.getNotificationType().equals("FriendRequest")) {
            return VIEW_TYPE_FRIEND_REQUEST;
        }
        else if(notification.getNotificationType().equals("NewMessage")) {
            return VIEW_TYPE_NEW_MESSAGE;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_FRIEND_REQUEST) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification_friend_request, parent, false);
            return new FriendRequestNotificationHolder(view);
        }
        else if (viewType == VIEW_TYPE_NEW_MESSAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification_new_message, parent, false);
            return new NewMessageNotificationHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Notification notification = (Notification) notificationList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_FRIEND_REQUEST:
                ((NotificationAdapter.FriendRequestNotificationHolder) holder).bind(notification, position);
                break;
            case VIEW_TYPE_NEW_MESSAGE:
                ((NotificationAdapter.NewMessageNotificationHolder) holder).bind(notification, position);
        }
    }


    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    private class FriendRequestNotificationHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTimestamps;
        AppCompatButton btnAccept, btnDeny;

        FriendRequestNotificationHolder(View itemView) {
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


    private class NewMessageNotificationHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTimestamps;
        ImageButton btnMore;
        NewMessageNotificationHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.title);
            tvContent = (TextView) itemView.findViewById(R.id.content);
            tvTimestamps = (TextView) itemView.findViewById(R.id.timestamps);
            btnMore = (ImageButton) itemView.findViewById(R.id.btn_more);
        }

        void bind(Notification notification, int position) {
            tvTitle.setText(notification.getTitle());
            tvContent.setText(notification.getContent());
            tvTimestamps.setText(notification.getTimeCreated());

            btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.OnShowingOption(position);
                }
            });
        }
    }
}