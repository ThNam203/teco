package com.worthybitbuilders.squadsense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.MinimizedProjectModel;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.CustomUtils;

import java.util.ArrayList;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter {
    private List<MinimizedProjectModel> data;
    private ProjectItemViewHolder.ClickHandler handler;

    public ProjectAdapter(List<MinimizedProjectModel> data, ProjectItemViewHolder.ClickHandler handler) {
        this.data = data;
        this.handler = handler;
    }

    public void setData(List<MinimizedProjectModel> data) {
        this.data.addAll(data);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_project_item_view, parent, false);
        return new ProjectItemViewHolder(layout, this.handler);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MinimizedProjectModel minimizedProjectModel = (MinimizedProjectModel) data.get(position);
        ((ProjectAdapter.ProjectItemViewHolder) holder).bind(minimizedProjectModel);
    }

    @Override
    public int getItemCount() {
        if (data != null) return data.size();
        return 0;
    }

    public static class ProjectItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvLastUpdate;
        private final ClickHandler handler;

        public ProjectItemViewHolder(@NonNull View itemView, ClickHandler handler) {
            super(itemView);
            this.tvTitle = itemView.findViewById(R.id.projectTitle);
            this.tvLastUpdate = itemView.findViewById(R.id.projectLastUpdate);
            this.handler = handler;
        }

        public void bind(MinimizedProjectModel projectModel) {
            tvTitle.setText(projectModel.getTitle());

            String dateString = CustomUtils.mongooseDateToFormattedString(projectModel.getUpdatedAt());
            tvLastUpdate.setText("Last updated: " + dateString);
            itemView.setOnClickListener(view -> this.handler.onClick(projectModel.get_id()));
        }

        public interface ClickHandler {
            void onClick(String _id);
        }
    }
}
