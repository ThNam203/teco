package com.worthybitbuilders.squadsense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.ChatRoom;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;

import java.util.List;
import java.util.Objects;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {
    private final List<ChatRoom> chatRooms;
    private final ChatRoomClickHandlers handlers;

    public ChatRoomAdapter(List<ChatRoom> chatRooms, ChatRoomClickHandlers handlers) {
        this.chatRooms = chatRooms;
        this.handlers = handlers;
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_item_view, parent, false);
        return new ChatRoomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        holder.bind(chatRooms.get(position), this.handlers);
    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    public static class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivChatRoomImage;
        private final TextView tvChatRoomTitle;
        private final TextView tvLastMessage;

        public ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            ivChatRoomImage = itemView.findViewById(R.id.chatRoomImage);
            tvChatRoomTitle = itemView.findViewById(R.id.tvChatRoomTitle);
            tvLastMessage = itemView.findViewById(R.id.tvChatRoomLastMessage);
        }

        public void bind(ChatRoom chatRoom, ChatRoomClickHandlers handlers) {
            itemView.setOnClickListener(view -> handlers.onChatRoomClick(chatRoom));
            tvLastMessage.setText(chatRoom.getLastMessage());

            if (chatRoom.getTitle() == null || chatRoom.getTitle().isEmpty()) {
                String otherUserName = null;
                String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
                // get the user that is different from the current user to take name
                for (int i = 0; i < chatRoom.getMembers().size(); i++) {
                    if (!Objects.equals(chatRoom.getMembers().get(i).get_id(), userId)) {
                        otherUserName = chatRoom.getMembers().get(i).getName();
                    }
                }
                tvChatRoomTitle.setText(otherUserName);
            } else tvChatRoomTitle.setText(chatRoom.getTitle());

            if (chatRoom.getLogoPath() != null && !chatRoom.getLogoPath().isEmpty())
                Glide.with(ivChatRoomImage)
                    .load(chatRoom.getLogoPath())
                    .placeholder(R.drawable.ic_chat_room_default_icon)
                    .into(ivChatRoomImage);
            else {
                String imagePath = null;
                String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
                // get the first user that is different from the current user to take the image
                for (int i = 0; i < chatRoom.getMembers().size(); i++) {
                    if (!Objects.equals(chatRoom.getMembers().get(i).get_id(), userId)) {
                        imagePath = chatRoom.getMembers().get(i).getImageProfilePath();
                    }
                }

                Glide.with(ivChatRoomImage)
                        .load(imagePath)
                        .placeholder(R.drawable.ic_user)
                        .into(ivChatRoomImage);
            }
        }
    }

    public interface ChatRoomClickHandlers {
        void onChatRoomClick(ChatRoom chatRoom);
    }
}
