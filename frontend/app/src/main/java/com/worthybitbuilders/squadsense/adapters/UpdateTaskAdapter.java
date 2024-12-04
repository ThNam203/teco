package com.worthybitbuilders.squadsense.adapters;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.activities.ShowImagesActivity;
import com.worthybitbuilders.squadsense.activities.ShowVideoActivity;
import com.worthybitbuilders.squadsense.activities.UpdateTaskCommentActivity;
import com.worthybitbuilders.squadsense.databinding.UpdateTaskMoreOptionsBinding;
import com.worthybitbuilders.squadsense.databinding.UpdateTaskViewBinding;
import com.worthybitbuilders.squadsense.models.UpdateTask;
import com.worthybitbuilders.squadsense.utils.CustomUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.BoardDetailItemViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateTaskAdapter extends RecyclerView.Adapter<UpdateTaskAdapter.UpdateTaskViewHolder> {
    private final Context context;
    private final BoardDetailItemViewModel viewModel;
    private List<UpdateTask> updateTasks = new ArrayList<>();
    private final Handlers handlers;

    private boolean isReadOnly = false;
    public UpdateTaskAdapter(Context context, BoardDetailItemViewModel viewModel, Handlers handlers) {
        this.context = context;
        this.viewModel = viewModel;
        this.handlers = handlers;
    }

    public void setReadOnly(boolean readOnly) {
        this.isReadOnly = readOnly;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<UpdateTask> updateTasks) {
        this.updateTasks = updateTasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UpdateTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UpdateTaskViewBinding itemBinding = UpdateTaskViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UpdateTaskViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateTaskViewHolder holder, int position) {
        holder.bind(updateTasks.get(position), position);
    }

    @Override
    public int getItemCount() {
        return updateTasks.size();
    }

    public class UpdateTaskViewHolder extends RecyclerView.ViewHolder {
        private final UpdateTaskViewBinding itemBinding;
        public UpdateTaskViewHolder(@NonNull UpdateTaskViewBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public void bind(UpdateTask task, int position) {
            Glide.with(context)
                    .load(task.getAuthorImagePath())
                    .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_user))
                    .into(itemBinding.ivAuthorAvatar);

            itemBinding.tvAuthorName.setText(task.getAuthorName() != null ? task.getAuthorName() : task.getAuthorEmail());
            itemBinding.tvTimestamp.setText(CustomUtils.mongooseDateToFormattedString(task.getCreatedAt()));

            String taskContent = task.getContent();
            if (taskContent.isEmpty()) itemBinding.tvTaskContent.setVisibility(View.GONE);
            else itemBinding.tvTaskContent.setText(taskContent);

            String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
            if(userId.equals(task.getAuthorId())) itemBinding.btnMoreOptions.setVisibility(View.VISIBLE);
            else itemBinding.btnMoreOptions.setVisibility(View.GONE);
            itemBinding.btnMoreOptions.setOnClickListener(view -> showMoreOptions(task, position));
            itemBinding.btnComment.setOnClickListener(view -> {
                Intent commentIntent = new Intent(context, UpdateTaskCommentActivity.class);
                commentIntent.putExtra("updateTaskId", updateTasks.get(position).get_id());
                commentIntent.putExtra("projectId", viewModel.getProjectId());
                commentIntent.putExtra("boardId", viewModel.getBoardId());
                commentIntent.putExtra("cellId", viewModel.getUpdateCellId());
                context.startActivity(commentIntent);
            });
            setUpButtonLike(task, position);
            setUpImageRecyclerView(task);
            setUpFileRecyclerView(task);
            setUpVideoRecyclerView(task);

            if(isReadOnly) {
                itemBinding.btnLike.setEnabled(false);
                itemBinding.btnComment.setEnabled(false);
                itemBinding.btnMoreOptions.setVisibility(View.GONE);

                itemView.setOnClickListener(view -> {
                    ToastUtils.showToastError(itemView.getContext(), "This task is already completed and cannot be edited", Toast.LENGTH_SHORT);
                });
            }
            else {
                itemBinding.btnLike.setEnabled(true);
                itemBinding.btnComment.setEnabled(true);
                itemBinding.btnMoreOptions.setVisibility(View.VISIBLE);

                itemView.setOnClickListener(null);
            }
        }

        private void showMoreOptions(UpdateTask task, int position) {
            UpdateTaskMoreOptionsBinding binding = UpdateTaskMoreOptionsBinding.inflate(LayoutInflater.from(context));
            PopupWindow popupWindow = new PopupWindow(binding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);

            binding.btnRemove.setOnClickListener(view -> {
                viewModel.deleteUpdateTask(task.getCellId(), task.get_id()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            updateTasks.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, updateTasks.size());
                            if (updateTasks.size() == 0) handlers.onAllUpdateTasksDeleted();
                        } else ToastUtils.showToastError(context, response.message(), Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        ToastUtils.showToastError(context, "Unable to delete, try again", Toast.LENGTH_SHORT);
                    }
                });

                popupWindow.dismiss();
            });

            // align the drop down
            binding.getRoot().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int xOffset = -(binding.getRoot().getMeasuredWidth() - itemBinding.btnMoreOptions.getWidth());

            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAsDropDown(itemBinding.btnMoreOptions, xOffset, -50);
        }

        private void setUpButtonLike(UpdateTask task, int position) {
            itemBinding.btnLike.setOnClickListener(view -> {
                viewModel.toggleLikeUpdateTask(task.getCellId(), task.get_id()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            task.setLiked(!task.isLiked());
                            if (task.isLiked()) task.setLikeCount(task.getLikeCount() + 1);
                            else task.setLikeCount(task.getLikeCount() - 1);
                            notifyItemChanged(position);
                        } else if (context != null) {
                            ToastUtils.showToastError(context, response.message(), Toast.LENGTH_SHORT);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        if (context != null) {
                            ToastUtils.showToastError(context, "Something went wrong", Toast.LENGTH_SHORT);
                        }
                    }
                });
            });

            changeLikeButtonText(task);
        }

        private void changeLikeButtonText(UpdateTask task) {
            if (task.isLiked()) {
                int color = Color.parseColor("#0073ea");
                itemBinding.btnLike.setIconTint(ColorStateList.valueOf(color));

                if (task.getLikeCount() == 1) {
                    itemBinding.btnLike.setText("Liked");
                } else itemBinding.btnLike.setText(String.format(Locale.US, "You, %d others liked", task.getLikeCount() - 1));
            } else {
                int color = ContextCompat.getColor(context, R.color.primary_icon_color);
                itemBinding.btnLike.setText(String.format(Locale.US, "%d Like", task.getLikeCount()));
                itemBinding.btnLike.setIconTint(ColorStateList.valueOf(color));
            }
        }

        private void setUpImageRecyclerView(UpdateTask task) {
            List<UpdateTaskImageAdapter.TaskImageFile> imageFiles = new ArrayList<>();
            List<UpdateTask.UpdateTaskFile> allFiles = task.getFiles();
            for (int i = 0; i < allFiles.size(); i++) {
                UpdateTask.UpdateTaskFile file = allFiles.get(i);
                if (Objects.equals(file.fileType, "Image"))
                    imageFiles.add(new UpdateTaskImageAdapter.TaskImageFile(file.location, file.name));
            }

            if (imageFiles.size() > 0) {
                UpdateTaskImageAdapter adapter = new UpdateTaskImageAdapter(context, imageFiles, position -> {
                    Intent showImagesIntent = new Intent(context, ShowImagesActivity.class);

                    ArrayList<String> imagePathsArrayList = new ArrayList<>();
                    ArrayList<String> imageNamesArrayList = new ArrayList<>();
                    for (int i = 0; i < imageFiles.size(); i++) {
                        imagePathsArrayList.add(imageFiles.get(i).location);
                        imageNamesArrayList.add(imageFiles.get(i).name);
                    }

                    showImagesIntent.putStringArrayListExtra("imagePaths", imagePathsArrayList);
                    showImagesIntent.putStringArrayListExtra("imageNames", imageNamesArrayList);
                    context.startActivity(showImagesIntent);
                });
                itemBinding.rvImageFiles.setVisibility(View.VISIBLE);
                itemBinding.rvImageFiles.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                itemBinding.rvImageFiles.setAdapter(adapter);
            }
        }

        private void setUpFileRecyclerView(UpdateTask task) {
            List<UpdateTaskFileAdapter.TaskFile> files = new ArrayList<>();
            List<UpdateTask.UpdateTaskFile> allFiles = task.getFiles();
            for (int i = 0; i < allFiles.size(); i++) {
                UpdateTask.UpdateTaskFile file = allFiles.get(i);
                if (Objects.equals(file.fileType, "Document"))
                    files.add(new UpdateTaskFileAdapter.TaskFile(file.location, file.name));
            }

            if (files.size() > 0) {
                UpdateTaskFileAdapter adapter = new UpdateTaskFileAdapter(files, position -> {
                    UpdateTaskFileAdapter.TaskFile file = files.get(position);
                    Uri fileUri = Uri.parse(file.location);
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(fileUri);
                    request.setTitle(file.name);
                    request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, file.name);
                    ToastUtils.showToastSuccess(context, "Started download", Toast.LENGTH_LONG);
                    downloadManager.enqueue(request);
                });

                itemBinding.rvFiles.setVisibility(View.VISIBLE);
                itemBinding.rvFiles.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                itemBinding.rvFiles.setAdapter(adapter);
            }
        }

        private void setUpVideoRecyclerView(UpdateTask task) {
            List<UpdateTaskVideoAdapter.TaskVideoFile> videoFiles = new ArrayList<>();
            List<UpdateTask.UpdateTaskFile> allFiles = task.getFiles();
            for (int i = 0; i < allFiles.size(); i++) {
                UpdateTask.UpdateTaskFile file = allFiles.get(i);
                if (Objects.equals(file.fileType, "Video"))
                    videoFiles.add(new UpdateTaskVideoAdapter.TaskVideoFile(file.location, file.name));
            }

            if (videoFiles.size() > 0) {
                UpdateTaskVideoAdapter adapter = new UpdateTaskVideoAdapter(context, videoFiles, position -> {
                    UpdateTaskVideoAdapter.TaskVideoFile file = videoFiles.get(position);
                    Intent showVideoIntent = new Intent(context, ShowVideoActivity.class);
                    showVideoIntent.putExtra("videoPath", file.location);
                    showVideoIntent.putExtra("videoName", file.name);
                    context.startActivity(showVideoIntent);
                });

                itemBinding.rvVideoFiles.setVisibility(View.VISIBLE);
                itemBinding.rvVideoFiles.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                itemBinding.rvVideoFiles.setAdapter(adapter);
            }
        }
    }

    public interface Handlers {
        void onAllUpdateTasksDeleted();
    }
}
