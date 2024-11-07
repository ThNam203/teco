package com.worthybitbuilders.squadsense.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.activities.InboxActivity;
import com.worthybitbuilders.squadsense.models.Notification;
import com.worthybitbuilders.squadsense.utils.ActivityUtils;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_FRIEND_REQUEST = 0;
    private static final int VIEW_TYPE_NEW_MESSAGE = 1;
    private static final int VIEW_TYPE_MEMBER_REQUEST = 2;
    private static final int VIEW_TYPE_ADMIN_REQUEST = 3;
    private List<Notification> notificationList;
    private OnActionCallback actionCallback;
    private OnReplyCallback replyCallback;

    public interface OnActionCallback {
        void OnClick(int position);
        void OnShowingOption(int position);
    }

    public interface OnReplyCallback{
        void OnAccept(int position, String NOTIFICATION_TYPE);
        void OnDeny(int position, String NOTIFICATION_TYPE);
    }

    public NotificationAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    public void setOnActionListener(OnActionCallback actionCallback)
    {
        this.actionCallback = actionCallback;
    }
    public void setOnReplyListener(OnReplyCallback replyCallback)
    {
        this.replyCallback = replyCallback;
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
        else if(notification.getNotificationType().equals("MemberRequest")) {
            return VIEW_TYPE_MEMBER_REQUEST;
        }
        else if(notification.getNotificationType().equals("AdminRequest")) {
            return VIEW_TYPE_ADMIN_REQUEST;
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
        else if (viewType == VIEW_TYPE_MEMBER_REQUEST) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification_member_request, parent, false);
            return new MemberRequestNotificationHolder(view);
        }
        else if (viewType == VIEW_TYPE_ADMIN_REQUEST) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification_admin_request, parent, false);
            return new AdminRequestNotificationHolder(view);
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
                break;
            case VIEW_TYPE_MEMBER_REQUEST:
                ((NotificationAdapter.MemberRequestNotificationHolder) holder).bind(notification, position);
                break;
            case VIEW_TYPE_ADMIN_REQUEST:
                ((NotificationAdapter.AdminRequestNotificationHolder) holder).bind(notification, position);
        }
    }


    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    private class FriendRequestNotificationHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTimestamps;
        ImageButton btnMore;
        FriendRequestNotificationHolder(View itemView) {
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionCallback.OnClick(position);
                }
            });

            btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionCallback.OnShowingOption(position);
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
                    actionCallback.OnShowingOption(position);
                }
            });
        }
    }

    private class MemberRequestNotificationHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTimestamps;

        AppCompatButton btnAccept, btnDeny;

        MemberRequestNotificationHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.title);
            tvContent = (TextView) itemView.findViewById(R.id.content);
            tvTimestamps = (TextView) itemView.findViewById(R.id.timestamps);

            btnAccept = (AppCompatButton) itemView.findViewById(R.id.btn_accept);
            btnDeny =(AppCompatButton) itemView.findViewById(R.id.btn_deny);
        }

        void bind(Notification notification, int position) {
            tvTitle.setText(notification.getTitle());
            tvContent.setText(notification.getContent());
            tvTimestamps.setText(notification.getTimeCreated());

            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    replyCallback.OnAccept(position, notification.getNotificationType());
                }
            });

            btnDeny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    replyCallback.OnDeny(position, notification.getNotificationType());
                }
            });
        }
    }

    private class AdminRequestNotificationHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTimestamps;

        AppCompatButton btnAccept, btnDeny;

        AdminRequestNotificationHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.title);
            tvContent = (TextView) itemView.findViewById(R.id.content);
            tvTimestamps = (TextView) itemView.findViewById(R.id.timestamps);

            btnAccept = (AppCompatButton) itemView.findViewById(R.id.btn_accept);
            btnDeny =(AppCompatButton) itemView.findViewById(R.id.btn_deny);
        }

        void bind(Notification notification, int position) {
            tvTitle.setText(notification.getTitle());
            tvContent.setText(notification.getContent());
            tvTimestamps.setText(notification.getTimeCreated());

            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    replyCallback.OnAccept(position, notification.getNotificationType());
                }
            });

            btnDeny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    replyCallback.OnDeny(position, notification.getNotificationType());
                }
            });
        }
    }
}