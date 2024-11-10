package com.worthybitbuilders.squadsense.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ItemFilterTypeBinding;
import com.worthybitbuilders.squadsense.databinding.ItemSearchFriendBinding;
import com.worthybitbuilders.squadsense.models.UserModel;

import java.util.List;

public class SearchingFriendAdapter extends RecyclerView.Adapter {
    private List<UserModel> listSearching;

    private ClickHandler handler;

    public SearchingFriendAdapter(List<UserModel> listSearching) {
        this.listSearching = listSearching;
    }

    public interface ClickHandler{
        void onClick(int position);
    }

    public void setOnClickItemFriend(ClickHandler handler)
    {
        this.handler = handler;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchFriendBinding binding = ItemSearchFriendBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemSearchingFriendHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserModel searchingFriend = (UserModel) listSearching.get(position);
        ((ItemSearchingFriendHolder) holder).bind(searchingFriend, position);
    }


    @Override
    public int getItemCount() {
        return listSearching.size();
    }

    private class ItemSearchingFriendHolder extends RecyclerView.ViewHolder {
        ItemSearchFriendBinding binding;
        ItemSearchingFriendHolder(@NonNull ItemSearchFriendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(UserModel searchingFriend, int position) {
            Glide.with(itemView.getContext())
                    .load(searchingFriend.getProfileImagePath())
                    .placeholder(R.drawable.ic_user)
                    .into(binding.searchingAvatar);
            binding.searchingName.setText(searchingFriend.getName());
            binding.searchingEmail.setText(searchingFriend.getEmail());
            itemView.setOnClickListener(view -> handler.onClick(position));
        }
    }
}