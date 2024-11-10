package com.worthybitbuilders.squadsense.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.worthybitbuilders.squadsense.models.UpdateTask;
import com.worthybitbuilders.squadsense.models.UpdateTaskAndCommentModel;
import com.worthybitbuilders.squadsense.services.ProjectService;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateTaskCommentViewModel extends ViewModel {
    private UpdateTask updateTask;
    private List<UpdateTaskAndCommentModel.UpdateTaskComment> comments;
    private final ProjectService projectService = RetrofitServices.getProjectService();
    private final String projectId;
    private final String boardId;
    private final String cellId;
    private final String updateTaskId;

    public UpdateTaskCommentViewModel(String projectId, String boardId, String cellId, String updateTaskId) {
        this.projectId = projectId;
        this.boardId = boardId;
        this.cellId = cellId;
        this.updateTaskId = updateTaskId;
    }

    public void getUpdateTaskAndComment(ApiCallHandler handler) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<UpdateTaskAndCommentModel> call = projectService.getUpdateTaskAndComment(userId, projectId, boardId, cellId, updateTaskId);
        call.enqueue(new Callback<UpdateTaskAndCommentModel>() {
            @Override
            public void onResponse(@NonNull Call<UpdateTaskAndCommentModel> call, @NonNull Response<UpdateTaskAndCommentModel> response) {
                if (response.isSuccessful()) {
                    updateTask = response.body().getUpdateTask();
                    comments = response.body().getComments();
                    handler.onSuccess();
                } else {
                    handler.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UpdateTaskAndCommentModel> call, @NonNull Throwable t) {
                handler.onFailure(t.getMessage());
            }
        });
    }

    public void uploadNewComment(List<MultipartBody.Part> parts, UpdateTaskAndCommentModel.UpdateTaskComment commentContent, ApiCallHandler handler) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<UpdateTaskAndCommentModel.UpdateTaskComment> call = projectService.createNewCommentToRemote(userId, projectId, boardId, cellId, updateTaskId, parts, commentContent);
        call.enqueue(new Callback<UpdateTaskAndCommentModel.UpdateTaskComment>() {
            @Override
            public void onResponse(@NonNull Call<UpdateTaskAndCommentModel.UpdateTaskComment> call, @NonNull Response<UpdateTaskAndCommentModel.UpdateTaskComment> response) {
                if (response.isSuccessful()) {
                    comments.add(response.body());
                    handler.onSuccess();
                }
                else handler.onFailure(response.message());
            }

            @Override
            public void onFailure(@NonNull Call<UpdateTaskAndCommentModel.UpdateTaskComment> call, @NonNull Throwable t) {
                handler.onFailure(t.getMessage());
            }
        });
    }

    public void deleteComment(String commentId, ApiCallHandler handler) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        projectService.deleteComment(userId, projectId, boardId, cellId, updateTaskId, commentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    comments.removeIf(comment -> Objects.equals(comment.get_id(), commentId));
                    handler.onSuccess();
                } else {
                    handler.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handler.onFailure(t.getMessage());
            }
        });
    }

    public Call<Void> toggleLikeUpdateTask() {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        return projectService.toggleLikeUpdateTask(userId, projectId, boardId, cellId, updateTaskId);
    }

    public Call<Void> toggleCommentLike(String commentId) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        return projectService.toggleLikeUpdateTaskComment(userId, projectId, boardId, cellId, updateTaskId, commentId);
    }

    public UpdateTask getUpdateTask() {
        return updateTask;
    }

    public List<UpdateTaskAndCommentModel.UpdateTaskComment> getComments() {
        return comments;
    }

    public interface ApiCallHandler {
        public void onSuccess();
        public void onFailure(String message);
    }
}
