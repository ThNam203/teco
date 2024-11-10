package com.worthybitbuilders.squadsense.adapters;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.worthybitbuilders.squadsense.databinding.ConfirmDeleteViewBinding;
import com.worthybitbuilders.squadsense.databinding.RowMoreOptionsBinding;
import com.worthybitbuilders.squadsense.databinding.UpdateTaskCommentViewBinding;
import com.worthybitbuilders.squadsense.databinding.UpdateTaskMoreOptionsBinding;
import com.worthybitbuilders.squadsense.models.UpdateTaskAndCommentModel;
import com.worthybitbuilders.squadsense.utils.CustomUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.ProjectActivityViewModel;
import com.worthybitbuilders.squadsense.viewmodels.UpdateTaskCommentViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateTaskCommentAdapter extends RecyclerView.Adapter<UpdateTaskCommentAdapter.UpdateTaskCommentViewHolder> {
    private final Context context;
    private final UpdateTaskCommentViewModel viewModel;
    private final List<UpdateTaskAndCommentModel.UpdateTaskComment> comments;

    public UpdateTaskCommentAdapter(Context context, UpdateTaskCommentViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
        this.comments = viewModel.getComments();
    }

    @NonNull
    @Override
    public UpdateTaskCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UpdateTaskCommentViewBinding binding = UpdateTaskCommentViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UpdateTaskCommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateTaskCommentViewHolder holder, int position) {
        holder.bind(comments.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (comments == null) return 0;
        else return comments.size();
    }

    public class UpdateTaskCommentViewHolder extends RecyclerView.ViewHolder {
        private UpdateTaskCommentViewBinding itemBinding;
        public UpdateTaskCommentViewHolder(@NonNull UpdateTaskCommentViewBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;
        }

        public void bind(UpdateTaskAndCommentModel.UpdateTaskComment comment, int position) {
            Glide.with(context)
                    .load(comment.getAuthor().profileImagePath)
                    .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_user))
                    .into(itemBinding.ivAuthorAvatar);

            itemBinding.tvAuthorName.setText(comment.getAuthor().name != null ? comment.getAuthor().name : comment.getAuthor().email);
            itemBinding.tvTimestamp.setText(CustomUtils.mongooseDateToFormattedString(comment.getCreatedAt()));

            String taskContent = comment.getContent();
            if (taskContent.isEmpty()) itemBinding.tvTaskContent.setVisibility(View.GONE);
            else itemBinding.tvTaskContent.setText(taskContent);

            String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
            if (!comment.getAuthor()._id.equals(userId)) itemBinding.btnMoreOptions.setVisibility(View.GONE);

            itemBinding.btnMoreOptions.setOnClickListener(view -> showMoreOptions(comment, position));
            setUpButtonLike(comment, position);
            setUpImageRecyclerView(comment);
            setUpFileRecyclerView(comment);
            setUpVideoRecyclerView(comment);
        }

        private void showMoreOptions(UpdateTaskAndCommentModel.UpdateTaskComment comment, int position) {
            UpdateTaskMoreOptionsBinding moreOptionsBinding = UpdateTaskMoreOptionsBinding.inflate(LayoutInflater.from(context));
            PopupWindow popupWindow = new PopupWindow(moreOptionsBinding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);

            moreOptionsBinding.btnRemove.setOnClickListener(view -> {
                showConfirmDelete(comment, position, popupWindow);
                popupWindow.dismiss();
            });

            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAsDropDown(itemBinding.btnMoreOptions, 0, 0);
        }

        private void showConfirmDelete(UpdateTaskAndCommentModel.UpdateTaskComment comment, int position, PopupWindow popupWindow) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            ConfirmDeleteViewBinding binding = ConfirmDeleteViewBinding.inflate(LayoutInflater.from(context));
            dialog.setContentView(binding.getRoot());
            binding.deleteTitle.setText("Delete");
            binding.etContent.setText("Are you sure to delete the update task");
            binding.btnClosePopup.setOnClickListener(view -> dialog.dismiss());
            binding.btnCancel.setOnClickListener(view -> dialog.dismiss());
            binding.btnAccept.setOnClickListener(view -> {
                viewModel.deleteComment(comment.get_id(), new UpdateTaskCommentViewModel.ApiCallHandler() {
                    @Override
                    public void onSuccess() {
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, comments.size());
                        popupWindow.dismiss();
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(String message) {
                        popupWindow.dismiss();
                        if (context != null) ToastUtils.showToastError(context, message, Toast.LENGTH_SHORT);
                    }
                });
            });

            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();
        }

        private void setUpButtonLike(UpdateTaskAndCommentModel.UpdateTaskComment comment, int position) {
            itemBinding.btnLike.setOnClickListener(view -> {
                viewModel.toggleCommentLike(comment.get_id()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            comment.setLiked(!comment.isLiked());
                            if (comment.isLiked()) comment.setLikeCount(comment.getLikeCount() + 1);
                            else comment.setLikeCount(comment.getLikeCount() - 1);
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

            if (comment.isLiked()) {
                int color = Color.parseColor("#0073ea");
                itemBinding.btnLike.setIconTint(ColorStateList.valueOf(color));

                if (comment.getLikeCount() == 1) {
                    itemBinding.btnLike.setText("Liked");
                } else itemBinding.btnLike.setText(String.format(Locale.US, "%d Liked", comment.getLikeCount() - 1));
            } else {
                int color = ContextCompat.getColor(context, R.color.primary_icon_color);
                itemBinding.btnLike.setText(String.format(Locale.US, "%d Like", comment.getLikeCount()));
                itemBinding.btnLike.setIconTint(ColorStateList.valueOf(color));
            }
        }

        private void setUpImageRecyclerView(UpdateTaskAndCommentModel.UpdateTaskComment comment) {
            List<UpdateTaskImageAdapter.TaskImageFile> imageFiles = new ArrayList<>();
            List<UpdateTaskAndCommentModel.UpdateTaskCommentFile> allFiles = comment.getFiles();
            for (int i = 0; i < allFiles.size(); i++) {
                UpdateTaskAndCommentModel.UpdateTaskCommentFile file = allFiles.get(i);
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

        private void setUpFileRecyclerView(UpdateTaskAndCommentModel.UpdateTaskComment comment) {
            List<UpdateTaskFileAdapter.TaskFile> files = new ArrayList<>();
            List<UpdateTaskAndCommentModel.UpdateTaskCommentFile> allFiles = comment.getFiles();
            for (int i = 0; i < allFiles.size(); i++) {
                UpdateTaskAndCommentModel.UpdateTaskCommentFile file = allFiles.get(i);
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

        private void setUpVideoRecyclerView(UpdateTaskAndCommentModel.UpdateTaskComment comment) {
            List<UpdateTaskVideoAdapter.TaskVideoFile> videoFiles = new ArrayList<>();
            List<UpdateTaskAndCommentModel.UpdateTaskCommentFile> allFiles = comment.getFiles();
            for (int i = 0; i < allFiles.size(); i++) {
                UpdateTaskAndCommentModel.UpdateTaskCommentFile file = allFiles.get(i);
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
}
