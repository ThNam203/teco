package com.worthybitbuilders.squadsense.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.R;

import java.io.File;
import java.util.List;

public class FileUpdateAdapter extends RecyclerView.Adapter<FileUpdateAdapter.FileUpdateItemViewHolder> {
    private List<Uri> data;
    ClickHandler handler;

    public FileUpdateAdapter(List<Uri> data, ClickHandler handler) {
        this.data = data;
        this.handler = handler;
    }

    @NonNull
    @Override
    public FileUpdateItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_task_file_view, parent, false);
        return new FileUpdateItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FileUpdateItemViewHolder holder, int position) {
        holder.bind(data.get(position), position, this.handler);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class FileUpdateItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivFileImage;
        private TextView tvFileName;
        private ImageButton btnRemoveFile;
        public FileUpdateItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ivFileImage = itemView.findViewById(R.id.ivFileImage);
            this.tvFileName = itemView.findViewById(R.id.tvFileName);
            this.btnRemoveFile = itemView.findViewById(R.id.btnRemoveFile);
        }

        public void bind(Uri fileUri, int position, ClickHandler handler) {
            if (isImageFile(fileUri)) {
                Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath());
                this.ivFileImage.setImageBitmap(bitmap);
            }

            this.tvFileName.setText(new File(fileUri.getPath()).getName());
            this.btnRemoveFile.setOnClickListener(view -> handler.onRemoveFile(position));
        }

        private boolean isImageFile(Uri fileUri) {
            String filePath = fileUri.getPath();
            String fileExtension = filePath.substring(filePath.lastIndexOf('.') + 1);
            String[] imageExtensions = {"jpg", "jpeg", "png", "gif", "bmp"};

            for (String extension : imageExtensions) {
                if (extension.equalsIgnoreCase(fileExtension)) {
                    return true;
                }
            }

            return false;
        }
    }

    public interface ClickHandler {
        void onRemoveFile(int position);
    }
}
