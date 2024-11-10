package com.worthybitbuilders.squadsense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ItemFilterCreatorBinding;
import com.worthybitbuilders.squadsense.models.ActivityLog;
import com.worthybitbuilders.squadsense.models.UserModel;

import java.util.List;

public class FilterCreatorAdapter extends RecyclerView.Adapter {
    private List<ActivityLog.ActivityLogCreator> listCreator;
    private List<ActivityLog.ActivityLogCreator> listSelectedCreator;

    public FilterCreatorAdapter(List<ActivityLog.ActivityLogCreator> listCreator, List<ActivityLog.ActivityLogCreator> listSelectedCreator) {
        this.listCreator = listCreator;
        this.listSelectedCreator = listSelectedCreator;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFilterCreatorBinding binding = ItemFilterCreatorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FilterCreatorHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ActivityLog.ActivityLogCreator creator = (ActivityLog.ActivityLogCreator) listCreator.get(position);
        ((FilterCreatorHolder) holder).bind(creator, position);
    }


    @Override
    public int getItemCount() {
        return listCreator.size();
    }

    private class FilterCreatorHolder extends RecyclerView.ViewHolder {
        ItemFilterCreatorBinding binding;
        FilterCreatorHolder(@NonNull ItemFilterCreatorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ActivityLog.ActivityLogCreator creator, int position) {
            Glide.with(itemView.getContext())
                    .load(creator.profileImagePath)
                    .placeholder(R.drawable.ic_user)
                    .into(binding.creatorAvatar);

            ChangeBackgroundOnSelected(creator);

            itemView.setOnClickListener(view -> {
                if(listSelectedCreator.contains(creator)) listSelectedCreator.remove(creator);
                else listSelectedCreator.add(creator);
                ChangeBackgroundOnSelected(creator);
            });
        }
        private void ChangeBackgroundOnSelected(ActivityLog.ActivityLogCreator creator)
        {
            if(listSelectedCreator.contains(creator)) binding.iconTick.setVisibility(View.VISIBLE);
            else binding.iconTick.setVisibility(View.GONE);
        }
    }
}