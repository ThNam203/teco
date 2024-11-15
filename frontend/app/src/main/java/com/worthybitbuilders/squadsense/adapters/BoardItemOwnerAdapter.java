package com.worthybitbuilders.squadsense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.viewmodels.ProjectActivityViewModel;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

import java.util.List;

public class BoardItemOwnerAdapter extends RecyclerView.Adapter{
    private final List<UserModel> listOwner;
    private OnActionCallback callback;

    private ProjectActivityViewModel projectActivityViewModel;

    public interface OnActionCallback {
        void OnClick(int position);
    }

    public BoardItemOwnerAdapter(List<UserModel> listOwner, ProjectActivityViewModel projectActivityViewModel) {
        this.listOwner = listOwner;
        this.projectActivityViewModel = projectActivityViewModel;
    }

    public BoardItemOwnerAdapter(List<UserModel> listOwner) {
        this.listOwner = listOwner;
    }

    public void setOnClickListener(OnActionCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ower, parent, false);
        return new BoardItemOwnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserModel owner = (UserModel) listOwner.get(position);
        ((BoardItemOwnerHolder) holder).bind(owner, position);
    }

    @Override
    public int getItemCount() {
        return listOwner.size();
    }

    private class BoardItemOwnerHolder extends RecyclerView.ViewHolder {
        ImageView ownerAvatar, btnDelete;
        TextView tvOwnerName;
        BoardItemOwnerHolder(View itemView) {
            super(itemView);
            ownerAvatar = itemView.findViewById(R.id.owner_avatar);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            tvOwnerName = itemView.findViewById(R.id.ownerName);
        }

        void bind(UserModel member, int position) {
            Glide.with(itemView.getContext())
                    .load(member.getProfileImagePath())
                    .placeholder(R.drawable.ic_user)
                    .into(ownerAvatar);
            tvOwnerName.setText(member.getName());
            if(projectActivityViewModel != null)
            {
                String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
                if(!projectActivityViewModel.getProjectModel().getCreatorId().equals(userId)
                        && !projectActivityViewModel.getProjectModel().getAdminIds().contains(userId))
                    btnDelete.setVisibility(View.GONE);
                else
                    btnDelete.setVisibility(View.VISIBLE);
            }

            btnDelete.setOnClickListener(view -> callback.OnClick(position));
        }
    }
}
