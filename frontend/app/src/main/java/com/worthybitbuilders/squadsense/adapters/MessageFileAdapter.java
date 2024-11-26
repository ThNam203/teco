package com.worthybitbuilders.squadsense.adapters;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.activities.ShowImagesActivity;
import com.worthybitbuilders.squadsense.activities.ShowVideoActivity;
import com.worthybitbuilders.squadsense.models.ChatMessage;
import com.worthybitbuilders.squadsense.utils.ImageUtils;
import com.worthybitbuilders.squadsense.utils.ToastUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MessageFileAdapter extends RecyclerView.Adapter<MessageFileAdapter.FileUpdateItemViewHolder> {
    private List<ChatMessage.MessageFile> data;
    Context context;

    public MessageFileAdapter(List<ChatMessage.MessageFile> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public FileUpdateItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_file_view, parent, false);
        return new FileUpdateItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FileUpdateItemViewHolder holder, int position) {
        holder.bind(data.get(position), position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class FileUpdateItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        private final ImageView ivFileImage;
        private final TextView tvFileName;
        public FileUpdateItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ivFileImage = itemView.findViewById(R.id.ivFileImage);
            this.tvFileName = itemView.findViewById(R.id.tvFileName);
            this.container = itemView.findViewById(R.id.fileContainer);
        }

        public void bind(ChatMessage.MessageFile file, int position) {
            tvFileName.setText(file.name);
            if (file.fileType.equals("Image")) {
                Glide.with(context).load(file.location).placeholder(R.drawable.ic_image).into(ivFileImage);
                container.setOnClickListener((view) -> {
                    Intent intent = new Intent(context, ShowImagesActivity.class);
                    ArrayList<String> imagePaths = new ArrayList<>();
                    imagePaths.add(file.location);
                    ArrayList<String> imageNames = new ArrayList<>();
                    imageNames.add(file.name);
                    intent.putStringArrayListExtra("imagePaths", imagePaths);
                    intent.putStringArrayListExtra("imageNames", imageNames);
                    context.startActivity(intent);
                });
            } else if (file.fileType.equals("Video")) {
                Glide.with(ivFileImage).load(file.location).placeholder(R.drawable.ic_video_play_circle).into(ivFileImage);
                container.setOnClickListener((view) -> {
                    Intent intent = new Intent(context, ShowVideoActivity.class);
                    intent.putExtra("videoPath", file.location);
                    intent.putExtra("videoName", file.name);
                    context.startActivity(intent);
                });
            } else if (file.fileType.equals("Document")) {
                ivFileImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_file));
                container.setOnClickListener((view) -> {
                    Uri fileUri = Uri.parse(file.location);
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(fileUri);
                    request.setTitle(file.name);
                    request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, file.name);
                    ToastUtils.showToastSuccess(context, "Started download", Toast.LENGTH_LONG);
                    downloadManager.enqueue(request);
                });
            }
        }
    }
}
