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
    private ProjectItemViewHolder.ClickHandler clickHandler;
    private ProjectItemViewHolder.LongClickHandler longClickHandler;

    public ProjectAdapter(List<MinimizedProjectModel> data,
                          ProjectItemViewHolder.ClickHandler clickHandler,
                          ProjectItemViewHolder.LongClickHandler longClickHandler) {
        this.data = data;
        this.clickHandler = clickHandler;
        this.longClickHandler = longClickHandler;
    }

    public void setData(List<MinimizedProjectModel> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_project_item_view, parent, false);
        return new ProjectItemViewHolder(layout, this.clickHandler, this.longClickHandler);
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
        private final ClickHandler clickHandler;
        private final LongClickHandler longClickHandler;


        public ProjectItemViewHolder(@NonNull View itemView, ClickHandler clickHandler, LongClickHandler longClickHandler) {
            super(itemView);
            this.tvTitle = itemView.findViewById(R.id.projectTitle);
            this.tvLastUpdate = itemView.findViewById(R.id.projectLastUpdate);
            this.clickHandler = clickHandler;
            this.longClickHandler = longClickHandler;
        }

        public void bind(MinimizedProjectModel projectModel) {
            tvTitle.setText(projectModel.getTitle());

            String dateString = CustomUtils.mongooseDateToFormattedString(projectModel.getUpdatedAt());
            tvLastUpdate.setText("Last updated: " + dateString);
            itemView.setOnClickListener(view -> this.clickHandler.onClick(projectModel.get_id()));
            itemView.setOnLongClickListener(view -> {
                if(longClickHandler != null)
                {
                    this.longClickHandler.onLongClick(view, projectModel.get_id());
                    return true;
                }
                return false;
            });
        }

        public interface ClickHandler {
            void onClick(String _id);
        }

        public interface LongClickHandler {
            void onLongClick(View view, String _id);
        }
    }
}
