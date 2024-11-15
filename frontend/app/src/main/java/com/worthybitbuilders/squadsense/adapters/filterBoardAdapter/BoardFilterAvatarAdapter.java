package com.worthybitbuilders.squadsense.adapters.filterBoardAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ItemFilterCreatorBinding;
import com.worthybitbuilders.squadsense.models.ActivityLog;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

import java.util.List;

public class BoardFilterAvatarAdapter extends RecyclerView.Adapter {
    private List<String> listId;
    private List<String> listSelectedId;

    private UserViewModel userViewModel;

    public BoardFilterAvatarAdapter(List<String> listId, List<String> listSelectedId) {
        this.listId = listId;
        this.listSelectedId = listSelectedId;
        userViewModel = new ViewModelProvider(new ViewModelStoreOwner() {
            @NonNull
            @Override
            public ViewModelStore getViewModelStore() {
                return new ViewModelStore();
            }
        }).get(UserViewModel.class);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFilterCreatorBinding binding = ItemFilterCreatorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BoardFilterAvatarHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String Id = (String) listId.get(position);
        ((BoardFilterAvatarHolder) holder).bind(Id, position);
    }


    @Override
    public int getItemCount() {
        return listId.size();
    }

    private class BoardFilterAvatarHolder extends RecyclerView.ViewHolder {
        ItemFilterCreatorBinding binding;
        BoardFilterAvatarHolder(@NonNull ItemFilterCreatorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String Id, int position) {
            userViewModel.getUserById(Id, new UserViewModel.UserCallback() {
                @Override
                public void onSuccess(UserModel user) {
                    binding.creatorName.setText(user.getName());
                    Glide.with(itemView.getContext())
                            .load(user.getProfileImagePath())
                            .placeholder(R.drawable.ic_user)
                            .into(binding.creatorAvatar);
                }

                @Override
                public void onFailure(String message) {

                }
            });

            ChangeBackgroundOnSelected(Id);

            itemView.setOnClickListener(view -> {
                if(listSelectedId.contains(Id)) listSelectedId.remove(Id);
                else listSelectedId.add(Id);
                ChangeBackgroundOnSelected(Id);
            });
        }
        private void ChangeBackgroundOnSelected(String Id)
        {
            if(listSelectedId.contains(Id)) binding.iconTick.setVisibility(View.VISIBLE);
            else binding.iconTick.setVisibility(View.GONE);
        }
    }
}