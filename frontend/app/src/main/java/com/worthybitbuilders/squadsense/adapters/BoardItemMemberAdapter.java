package com.worthybitbuilders.squadsense.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.UserModel;

import java.util.List;

public class BoardItemMemberAdapter extends RecyclerView.Adapter{
    private List<UserModel> memberList = null;
    private List<Boolean> statuses;
    private OnActionCallback callback;

    public interface OnActionCallback {
        void OnClick(int position, boolean status);
    }

    public BoardItemMemberAdapter() {}

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<UserModel> memberList, List<Boolean> statuses) {
        this.memberList = memberList;
        this.statuses = statuses;
        notifyDataSetChanged();
    }

    public void setStatusAt(int position, boolean status) {
        this.statuses.set(position, status);
        notifyItemChanged(position);
    }

    public void setOnClickListener(OnActionCallback callback) {
        this.callback = callback;
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
        // if the user is set before
        boolean isSet = statuses.get(position);
        ((BoardItemMemberHolder) holder).bind(member, isSet, position);
    }

    @Override
    public int getItemCount() {
        if (memberList == null) return 0;
        else return memberList.size();
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

        void bind(UserModel member, Boolean isSet, int position) {
            tvMemberName.setText(member.getName());
            checkBox.setChecked(isSet);

            Glide.with(itemView.getContext())
                    .load(member.getProfileImagePath())
                    .placeholder(R.drawable.ic_user)
                    .into(memberAvatar);
            checkBox.setOnClickListener(view -> callback.OnClick(position, checkBox.isChecked()));
            itemView.setOnClickListener(view -> checkBox.performClick());
        }
    }
}
