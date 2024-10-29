package com.worthybitbuilders.squadsense.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;

import java.util.List;

public class UpdateTaskVideoAdapter extends RecyclerView.Adapter<UpdateTaskVideoAdapter.UpdateTaskVideoView> {
    private final Context context;
    public static class TaskVideoFile {
        public String location;
        public String name;

        public TaskVideoFile(String location, String name) {
            this.location = location;
            this.name = name;
        }
    }
    private final List<TaskVideoFile> mediaPaths;
    private final ClickHandlers handlers;

    public UpdateTaskVideoAdapter(Context context, List<TaskVideoFile> mediaPaths, ClickHandlers handlers) {
        this.context = context;
        this.mediaPaths = mediaPaths;
        this.handlers = handlers;
    }

    @NonNull
    @Override
    public UpdateTaskVideoView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_task_video_view, parent, false);
        return new UpdateTaskVideoView(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateTaskVideoView holder, int position) {
        holder.bind(mediaPaths.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mediaPaths != null ? mediaPaths.size() : 0;
    }

    class UpdateTaskVideoView extends RecyclerView.ViewHolder {
        private final ImageView ivVideoThumbnail;
        private final TextView tvVideoName;
        public UpdateTaskVideoView(@NonNull View itemView) {
            super(itemView);
            this.ivVideoThumbnail = itemView.findViewById(R.id.ivVideoThumbnail);
            this.tvVideoName = itemView.findViewById(R.id.tvVideoName);
        }

        public void bind(TaskVideoFile imageFile, int position) {
            Glide
                    .with(context)
                    .load(imageFile.location)
                    .placeholder(R.drawable.ic_video_play_circle)
                    .into(ivVideoThumbnail);

            ivVideoThumbnail.setOnClickListener(view -> handlers.onClick(position));
            tvVideoName.setText(imageFile.name);
        }
    }

    public interface ClickHandlers {
        void onClick(int position);
    }
}
