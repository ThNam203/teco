package com.worthybitbuilders.squadsense.adapters.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.ChatRoom;
import java.util.List;

public class GroupChatMemberAdapter extends RecyclerView.Adapter<GroupChatMemberAdapter.MemberViewHolder> {
    private final List<ChatRoom.Member> members;
    private ClickHandler clickHandler;
    public GroupChatMemberAdapter(List<ChatRoom.Member> members) {
        this.members = members;
    }

    public void setClickHandler(ClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }


    @NonNull
    @Override
    public GroupChatMemberAdapter.MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        ChatRoom.Member member = members.get(position);
        holder.bind(member, position);
    }


    @Override
    public int getItemCount() {
        return members.size();
    }

    public class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView tvFriendName, tvFriendConnection;
        ImageView friendAvatar;
        ImageButton btnMore;
        MemberViewHolder(View itemView) {
            super(itemView);
            tvFriendName = itemView.findViewById(R.id.friend_name);
            tvFriendConnection = itemView.findViewById(R.id.friend_connection);
            friendAvatar = itemView.findViewById(R.id.friend_avatar);
            btnMore = itemView.findViewById(R.id.btn_more);
        }

        void bind(ChatRoom.Member member, int position) {
            tvFriendName.setText(member.getName());

            Glide.with(itemView.getContext())
                    .load(member.getProfileImagePath())
                    .placeholder(R.drawable.ic_user)
                    .into(friendAvatar);

            btnMore.setOnClickListener(view -> clickHandler.onMoreOptionsClick(position, btnMore));
        }
    }

    public interface ClickHandler {
        public void onMoreOptionsClick(int position, View anchor);
    }
}