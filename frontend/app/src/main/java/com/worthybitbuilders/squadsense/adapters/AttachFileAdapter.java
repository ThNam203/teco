package com.worthybitbuilders.squadsense.adapters;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.utils.ImageUtils;
import com.worthybitbuilders.squadsense.utils.ToastUtils;

import java.io.IOException;
import java.util.List;

public class AttachFileAdapter extends RecyclerView.Adapter<AttachFileAdapter.FileUpdateItemViewHolder> {
    private List<Uri> data;
    ClickHandler handler;
    Context mContext;

    public AttachFileAdapter(List<Uri> data, Context context, ClickHandler handler) {
        this.data = data;
        this.mContext = context;
        this.handler = handler;
    }

    @NonNull
    @Override
    public FileUpdateItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.attach_file_view, parent, false);
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
        private ShapeableImageView btnRemoveFile;
        public FileUpdateItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ivFileImage = itemView.findViewById(R.id.ivFileImage);
            this.tvFileName = itemView.findViewById(R.id.tvFileName);
            this.btnRemoveFile = itemView.findViewById(R.id.btnRemoveFile);
        }

        public void bind(Uri fileUri, int position, ClickHandler handler) {
            ContentResolver resolver = mContext.getContentResolver();
            String type = resolver.getType(fileUri);
            if (type != null && type.startsWith("image/")) {
                try {
                    Bitmap bitmap = ImageUtils.uriToBitmap(mContext, fileUri);
                    // Calculate the aspect ratio
                    float ratio = Math.min((float)70 / bitmap.getHeight(), (float)70 / bitmap.getWidth());
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, Math.round(bitmap.getWidth() * ratio), Math.round(bitmap.getHeight() * ratio), true);
                    this.ivFileImage.setImageBitmap(scaledBitmap);
                } catch (IOException e) {
                    ToastUtils.showToastError(mContext, "Unable to get the photo", Toast.LENGTH_LONG);
                }
            }

            this.tvFileName.setText(getFileName(fileUri));
            this.btnRemoveFile.setOnClickListener(view -> handler.onRemoveFile(position));
        }
    }

    public interface ClickHandler {
        void onRemoveFile(int position);
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
