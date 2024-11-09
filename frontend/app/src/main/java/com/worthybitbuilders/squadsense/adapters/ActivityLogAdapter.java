package com.worthybitbuilders.squadsense.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ActivityProjectActivityLogBinding;
import com.worthybitbuilders.squadsense.databinding.ProjectActivityLogViewBinding;
import com.worthybitbuilders.squadsense.models.ActivityLog;
import com.worthybitbuilders.squadsense.utils.CustomUtils;

import java.util.List;

public class ActivityLogAdapter extends RecyclerView.Adapter<ActivityLogAdapter.ActivityLogViewHolder> {
    private List<ActivityLog> data = null;

    public ActivityLogAdapter(List<ActivityLog> activityLogs) {
        this.data = activityLogs;
    }

    @NonNull
    @Override
    public ActivityLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProjectActivityLogViewBinding binding = ProjectActivityLogViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ActivityLogViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        if (data == null) return 0;
        else return data.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityLogViewHolder holder, int position) {
        holder.bind(data.get(position));
        if (position == 0) holder.binding.ivTopVerticalBar.setVisibility(View.GONE);
        if (position == data.size() - 1) holder.binding.ivBottomVerticalBar.setVisibility(View.GONE);
    }

    public static class ActivityLogViewHolder extends RecyclerView.ViewHolder {
        public ProjectActivityLogViewBinding binding;
        public ActivityLogViewHolder(@NonNull ProjectActivityLogViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ActivityLog activityLog) {
            binding.tvCreatorName.setText(activityLog.getCreator().name);
            binding.tvActivityLogDescription.setText(activityLog.getDescription());
            binding.tvProjectTitle.setText(activityLog.getProject().title);

            String time = CustomUtils.mongooseDateToFormattedString(activityLog.getCreatedDate());
            binding.tvActivityLogTime.setText(time);

            if (activityLog.getBoard() != null) {
                binding.projectTitleAndBoardTitleSeparator.setVisibility(View.VISIBLE);
                binding.tvBoardTitle.setVisibility(View.VISIBLE);
                binding.tvBoardTitle.setText(activityLog.getBoard().boardTitle);
            }

            binding.tvActivityLogType.setText(activityLog.getType());
            switch (activityLog.getType()) {
                case "Remove":
                    DrawableCompat.setTint(binding.tvActivityLogType.getBackground(), Color.parseColor("#e63946"));
                    break;
                case "New":
                    DrawableCompat.setTint(binding.tvActivityLogType.getBackground(), Color.parseColor("#ffca3a"));
                    break;
                case "Update":
                    DrawableCompat.setTint(binding.tvActivityLogType.getBackground(), Color.parseColor("#48cae4"));
                    break;
                case "Change":
                    DrawableCompat.setTint(binding.tvActivityLogType.getBackground(), Color.parseColor("#6930c3"));
                    break;
            }

            Glide
                .with(binding.ivCreatorAvatar)
                .load(activityLog.getCreator().profileImagePath)
                .placeholder(R.drawable.ic_user)
                .into(binding.ivCreatorAvatar);
        }
    }
}
