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

public class UpdateTaskImageAdapter extends RecyclerView.Adapter<UpdateTaskImageAdapter.UpdateTaskImageView> {
    private final Context context;
    public static class TaskImageFile {
        public String location;
        public String name;

        public TaskImageFile(String location, String name) {
            this.location = location;
            this.name = name;
        }
    }
    private final List<TaskImageFile> mediaPaths;
    private final ClickHandlers handlers;

    public UpdateTaskImageAdapter(Context context, List<TaskImageFile> mediaPaths, ClickHandlers handlers) {
        this.context = context;
        this.mediaPaths = mediaPaths;
        this.handlers = handlers;
    }

    @NonNull
    @Override
    public UpdateTaskImageView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_task_image_view, parent, false);
        return new UpdateTaskImageView(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateTaskImageView holder, int position) {
        holder.bind(mediaPaths.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mediaPaths != null ? mediaPaths.size() : 0;
    }

    class UpdateTaskImageView extends RecyclerView.ViewHolder {
        private final ImageView ivFileImage;
        private final TextView tvFileName;
        public UpdateTaskImageView(@NonNull View itemView) {
            super(itemView);
            this.ivFileImage = itemView.findViewById(R.id.ivFileImage);
            this.tvFileName = itemView.findViewById(R.id.tvFileName);
        }

        public void bind(TaskImageFile imageFile, int position) {
            Glide
                .with(context)
                .load(imageFile.location)
                .placeholder(R.drawable.ic_image)
                .into(ivFileImage);

            ivFileImage.setOnClickListener(view -> handlers.onClick(position));
            tvFileName.setText(imageFile.name);
        }
    }

    public interface ClickHandlers {
        void onClick(int position);
    }
}
