package com.worthybitbuilders.squadsense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.MinimizedProjectModel;
import com.worthybitbuilders.squadsense.utils.CustomUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectItemViewHolder> {
    private List<MinimizedProjectModel> data;
    private ProjectItemViewHolder.ClickHandler handler;

    public ProjectAdapter(List<MinimizedProjectModel> data, ProjectItemViewHolder.ClickHandler handler) {
        this.data = data;
        this.handler = handler;
    }

    public void setData(List<MinimizedProjectModel> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProjectItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_project_item_view, parent, false);
        return new ProjectItemViewHolder(layout, this.handler);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectItemViewHolder holder, int position) {
        holder.bind(data.get(position));
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

            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            Date formattedDate = null;
            try {
                formattedDate = sdf.parse(projectModel.getUpdatedAt());
            } catch (ParseException e) {
                throw new RuntimeException();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(formattedDate);
            String dateString = String.format(Locale.US, "%s %d, %d", CustomUtils.convertIntToMonth(calendar.get(Calendar.MONTH)),calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));
            tvLastUpdate.setText(dateString);
            itemView.setOnClickListener(view -> this.handler.onClick(projectModel.get_id()));
        }

        public interface ClickHandler {
            void onClick(String _id);
        }
    }
}
