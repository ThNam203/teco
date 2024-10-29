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

public class UpdateTaskFileAdapter extends RecyclerView.Adapter<UpdateTaskFileAdapter.UpdateTaskFileView> {
    public static class TaskFile {
        public String location;
        public String name;

        public TaskFile(String location, String name) {
            this.location = location;
            this.name = name;
        }
    }
    private final List<UpdateTaskFileAdapter.TaskFile> mediaPaths;
    private final UpdateTaskImageAdapter.ClickHandlers handlers;

    public UpdateTaskFileAdapter(List<UpdateTaskFileAdapter.TaskFile> mediaPaths, UpdateTaskImageAdapter.ClickHandlers handlers) {
        this.mediaPaths = mediaPaths;
        this.handlers = handlers;
    }

    @NonNull
    @Override
    public UpdateTaskFileAdapter.UpdateTaskFileView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_task_file_view, parent, false);
        return new UpdateTaskFileAdapter.UpdateTaskFileView(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateTaskFileAdapter.UpdateTaskFileView holder, int position) {
        holder.bind(mediaPaths.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mediaPaths != null ? mediaPaths.size() : 0;
    }

    class UpdateTaskFileView extends RecyclerView.ViewHolder {
        private final ImageView ivFileImage;
        private final TextView tvFileName;
        public UpdateTaskFileView(@NonNull View itemView) {
            super(itemView);
            this.ivFileImage = itemView.findViewById(R.id.ivFileImage);
            this.tvFileName = itemView.findViewById(R.id.tvFileName);
        }

        public void bind(UpdateTaskFileAdapter.TaskFile imageFile, int position) {
            ivFileImage.setOnClickListener(view -> handlers.onClick(position));
            tvFileName.setText(imageFile.name);
        }
    }

    public interface ClickHandlers {
        void onClick(int position);
    }
}
