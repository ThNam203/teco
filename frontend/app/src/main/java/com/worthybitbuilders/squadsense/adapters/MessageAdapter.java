package com.worthybitbuilders.squadsense.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.ChatMessage;
import com.worthybitbuilders.squadsense.utils.CustomUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
    private final Context mContext;
    private final List<ChatMessage> mMessageList;
    public MessageAdapter(Context context, List<ChatMessage> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = (ChatMessage) mMessageList.get(position);
        if (message.getSender()._id.equals(userId)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_user_view, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_other_view, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTimestamp;
        ShapeableImageView ivProfileImage;
        RecyclerView rvFiles;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvOtherMessage);
            tvTimestamp = itemView.findViewById(R.id.tvOtherTimestamp);
            ivProfileImage = itemView.findViewById(R.id.ivOtherAvatar);
            rvFiles = itemView.findViewById(R.id.rvOtherFiles);
        }

        void bind(ChatMessage message) {
            tvMessage.setText(message.getMessage());
            String formattedDate = CustomUtils.mongooseDateToFormattedString(message.getCreatedAt());
            tvTimestamp.setText(formattedDate);
            Glide.with(mContext)
                    .load(message.getSender().profileImagePath)
                    .placeholder(R.drawable.ic_user)
                    .into(ivProfileImage);

            if (message.getFiles() != null && message.getFiles().size() > 0) {
                MessageFileAdapter adapter = new MessageFileAdapter(message.getFiles(), mContext);
                rvFiles.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                rvFiles.setVisibility(View.VISIBLE);
                rvFiles.setAdapter(adapter);
            }
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTimestamp;
        RecyclerView rvFiles;

        SentMessageHolder(View itemView) {
            super(itemView);
            tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
            tvTimestamp = (TextView) itemView.findViewById(R.id.tvTimestamp);
            rvFiles = itemView.findViewById(R.id.rvFiles);
        }

        void bind(ChatMessage message) {
            tvMessage.setText(message.getMessage());
            String formattedDate = CustomUtils.mongooseDateToFormattedString(message.getCreatedAt());
            tvTimestamp.setText(formattedDate);

            if (message.getFiles() != null && message.getFiles().size() > 0) {
                MessageFileAdapter adapter = new MessageFileAdapter(message.getFiles(), mContext);
                rvFiles.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                rvFiles.setVisibility(View.VISIBLE);
                rvFiles.setAdapter(adapter);
            }
        }
    }
}