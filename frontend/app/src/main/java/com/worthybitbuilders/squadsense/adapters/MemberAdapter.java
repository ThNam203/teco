package com.worthybitbuilders.squadsense.adapters;

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
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter {
    private String creatorId;
    private final List<String> adminIds;
    private final List<UserModel> memberList;
    private OnActionCallback callback;

    public interface OnActionCallback {
        void OnClick(int position);

        void OnMoreOptionsClick(int position);
    }

    public MemberAdapter(List<UserModel> memberList, List<String> adminIds) {
        this.memberList = memberList;
        this.adminIds = adminIds;
    }

    public void setCreatorId(String creatorId)
    {
        this.creatorId = creatorId;
    }

    public void setOnClickListener(OnActionCallback callback) {
        this.callback = callback;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_member, parent, false);
            return new MemberHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserModel member = (UserModel) memberList.get(position);
        ((MemberAdapter.MemberHolder) holder).bind(member, position);
    }


    @Override
    public int getItemCount() {
        return memberList.size();
    }

    private class MemberHolder extends RecyclerView.ViewHolder {
        TextView tvMemberName, tvMemberRole;
        ImageView memberAvatar;
        ImageButton btnMore;

        MemberHolder(View itemView) {
            super(itemView);
            tvMemberName = itemView.findViewById(R.id.member_name);
            memberAvatar = itemView.findViewById(R.id.member_avatar);
            tvMemberRole = itemView.findViewById(R.id.member_role);
            btnMore = itemView.findViewById(R.id.btn_more);
        }

        void bind(UserModel member, int position) {
            //show icon admin
            if(creatorId.equals(member.getId()))
            {
                tvMemberRole.setText("Creator");
                tvMemberRole.setVisibility(View.VISIBLE);
            }
            else if(adminIds.contains(member.getId()))
            {
                tvMemberRole.setText("Admin");
                tvMemberRole.setVisibility(View.VISIBLE);
            }
            else tvMemberRole.setVisibility(View.GONE);

            String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);

            tvMemberName.setText(member.getName());
            if(userId.equals(member.getId()))
                tvMemberName.setText(tvMemberName.getText().toString() + " (You)");

            Glide.with(itemView.getContext())
                    .load(member.getProfileImagePath())
                    .placeholder(R.drawable.ic_user)
                    .into(memberAvatar);

            itemView.setOnClickListener(view -> callback.OnClick(position));
            btnMore.setOnClickListener(view -> callback.OnMoreOptionsClick(position));
        }
    }
}