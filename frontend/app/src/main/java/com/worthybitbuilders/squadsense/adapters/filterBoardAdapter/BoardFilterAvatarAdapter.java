package com.worthybitbuilders.squadsense.adapters.filterBoardAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ItemFilterCreatorBinding;
import com.worthybitbuilders.squadsense.models.ActivityLog;

import java.util.List;

public class BoardFilterAvatarAdapter extends RecyclerView.Adapter {
    private List<String> listAvatarPath;
    private List<String> listSelectedAvatar;

    public BoardFilterAvatarAdapter(List<String> listAvatarPath, List<String> listSelectedAvatar) {
        this.listAvatarPath = listAvatarPath;
        this.listSelectedAvatar = listSelectedAvatar;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFilterCreatorBinding binding = ItemFilterCreatorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BoardFilterAvatarHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String avatar = (String) listAvatarPath.get(position);
        ((BoardFilterAvatarHolder) holder).bind(avatar, position);
    }


    @Override
    public int getItemCount() {
        return listAvatarPath.size();
    }

    private class BoardFilterAvatarHolder extends RecyclerView.ViewHolder {
        ItemFilterCreatorBinding binding;
        BoardFilterAvatarHolder(@NonNull ItemFilterCreatorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String avatarPath, int position) {
            binding.creatorName.setText("Name");
            Glide.with(itemView.getContext())
                    .load(avatarPath)
                    .placeholder(R.drawable.ic_user)
                    .into(binding.creatorAvatar);

            ChangeBackgroundOnSelected(avatarPath);

            itemView.setOnClickListener(view -> {
                if(listSelectedAvatar.contains(avatarPath)) listSelectedAvatar.remove(avatarPath);
                else listSelectedAvatar.add(avatarPath);
                ChangeBackgroundOnSelected(avatarPath);
            });
        }
        private void ChangeBackgroundOnSelected(String avatar)
        {
            if(listSelectedAvatar.contains(avatar)) binding.iconTick.setVisibility(View.VISIBLE);
            else binding.iconTick.setVisibility(View.GONE);
        }
    }
}