package com.worthybitbuilders.squadsense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.UserModel;

import java.util.List;

public class BoardItemMemberAdapter extends RecyclerView.Adapter{
    private final List<UserModel> memberList;
    private OnActionCallback callback;

    public interface OnActionCallback {
        void OnClick(int position, boolean status);
    }

    public BoardItemMemberAdapter(List<UserModel> memberList) {
        this.memberList = memberList;
    }

    public void setOnClickListener(OnActionCallback callback) {
        this.callback = callback;
    }

    public void setOwner(int position, boolean status)
    {

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ower_member, parent, false);
        return new BoardItemMemberHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserModel member = (UserModel) memberList.get(position);
        ((BoardItemMemberHolder) holder).bind(member, position);
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    private class BoardItemMemberHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ImageView memberAvatar;
        TextView tvMemberName;
        BoardItemMemberHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            memberAvatar = itemView.findViewById(R.id.member_avatar);
            tvMemberName = itemView.findViewById(R.id.member_name);
        }

        void bind(UserModel member, int position) {
            tvMemberName.setText(member.getName());

            Glide.with(itemView.getContext())
                    .load(member.getProfileImagePath())
                    .placeholder(R.drawable.ic_user)
                    .into(memberAvatar);
            checkBox.setOnClickListener(view -> callback.OnClick(position, checkBox.isChecked()));
            itemView.setOnClickListener(view -> checkBox.performClick());
        }
    }
}
