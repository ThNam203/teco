package com.worthybitbuilders.squadsense.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.worthybitbuilders.squadsense.models.BoardDetailItemModel;
import com.worthybitbuilders.squadsense.models.UpdateTask;
import com.worthybitbuilders.squadsense.models.board_models.BoardBaseItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardCheckboxItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardDateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardNumberItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardStatusItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTextItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTimelineItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;
import com.worthybitbuilders.squadsense.services.ProjectService;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardDetailItemViewModel extends ViewModel {
    private BoardDetailItemModel data;
    private final int rowPosition;
    private final MutableLiveData<BoardDetailItemModel> itemsLiveData = new MutableLiveData<>(null);
    private List<UpdateTask> updateTasks;
    private final MutableLiveData<List<UpdateTask>> updateTasksLiveData = new MutableLiveData<>(null);
    private final ProjectService projectService = RetrofitServices.getProjectService();
    private final String projectId;
    private final String boardId;
    private final String projectTitle;
    private final String boardTitle;
    private String rowTitle;
    /**
     * @param rowPosition is the position according to the board,
     *                    we need it to update the exact cell on the remote
     */
    public BoardDetailItemViewModel(int rowPosition, String projectId, String boardId, String projectTitle, String boardTitle, String rowTitle) {
        this.rowPosition = rowPosition;
        this.projectId = projectId;
        this.boardId = boardId;
        this.projectTitle = projectTitle;
        this.boardTitle = boardTitle;
        this.rowTitle = rowTitle;
    }
    public int getRowPosition() {
        return rowPosition;
    }

    public String getProjectId() { return projectId; }

    public String getRowTitle() { return rowTitle; }

    public String getBoardId() { return boardId; }

    public String getProjectTitle() { return projectTitle; }

    public String getBoardTitle() { return boardTitle; }

    public MutableLiveData<BoardDetailItemModel> getItemsLiveData() {
        return itemsLiveData;
    }
    public MutableLiveData<List<UpdateTask>> getUpdateTasksLiveData() {
        return updateTasksLiveData;
    }

    public void getDataFromRemote(ApiCallHandler handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        projectService.getCellsInARow(userId, projectId, boardId, rowPosition).enqueue(new Callback<BoardDetailItemModel>() {
            @Override
            public void onResponse(@NonNull Call<BoardDetailItemModel> call, @NonNull Response<BoardDetailItemModel> response) {
                if (response.isSuccessful()) {
                    data = response.body();
                    itemsLiveData.setValue(data);
                    handlers.onSuccess();
                } else {
                    handlers.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<BoardDetailItemModel> call, Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }

    public Call<Void> toggleLikeUpdateTask(String cellId, String updateTaskId) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        return projectService.toggleLikeUpdateTask(userId, projectId, boardId, cellId, updateTaskId);
    }

    public Call<Void> deleteUpdateTask(String cellId, String updateTaskId) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        return projectService.deleteUpdateTask(userId, projectId, boardId, cellId, updateTaskId);
    }

    public void getUpdateTasksByCellId(String cellId, ApiCallHandler handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        projectService.getAllUpdateTasksOfACell(userId, projectId, boardId, cellId).enqueue(new Callback<List<UpdateTask>>() {
            @Override
            public void onResponse(@NonNull Call<List<UpdateTask>> call, @NonNull Response<List<UpdateTask>> response) {
                if (response.isSuccessful()) {
                    updateTasks = response.body();
                    updateTasksLiveData.setValue(updateTasks);
                    handlers.onSuccess();
                } else {
                    handlers.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UpdateTask>> call, @NonNull Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }

    public void updateRowTitle(String newTitle, ApiCallHandler handler) throws JSONException {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("newTitle", newTitle);
        Call<Void> call = projectService.updateRowTitle(userId, projectId, boardId, rowPosition, jsonObject);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    rowTitle = newTitle;
                    handler.onSuccess();
                } else handler.onFailure(response.message());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handler.onFailure(t.getMessage());
            }
        });
    }

    public void deleteRow(ApiCallHandler handler) throws JSONException {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<Void> call = projectService.deleteARow(userId, projectId, boardId, rowPosition);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    handler.onSuccess();
                } else handler.onFailure(response.message());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handler.onFailure(t.getMessage());
            }
        });
    }

    /**
     * This function DOESN'T update the board
     * Use it along with AN ADAPTER or pass it into the function
     */
    public Call<Void> updateACell(BoardBaseItemModel cellModel) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        switch (cellModel.getCellType()) {
            case "CellStatus":
                return projectService.updateCellToRemote(userId, projectId, boardId, cellModel.get_id(), (BoardStatusItemModel) cellModel);
            case "CellText":
                return projectService.updateCellToRemote(userId, projectId, boardId, cellModel.get_id(), (BoardTextItemModel) cellModel);
            case "CellNumber":
                return projectService.updateCellToRemote(userId, projectId, boardId, cellModel.get_id(), (BoardNumberItemModel) cellModel);
            case "CellTimeline":
                return projectService.updateCellToRemote(userId, projectId, boardId, cellModel.get_id(), (BoardTimelineItemModel) cellModel);
            case "CellDate":
                return projectService.updateCellToRemote(userId, projectId, boardId, cellModel.get_id(), (BoardDateItemModel) cellModel);
            case "CellUser":
                return projectService.updateCellToRemote(userId, projectId, boardId, cellModel.get_id(), (BoardUserItemModel) cellModel);
            case "CellCheckbox":
                return projectService.updateCellToRemote(userId, projectId, boardId, cellModel.get_id(), (BoardCheckboxItemModel) cellModel);
            default:
                throw new RuntimeException();
        }
    }

    public interface ApiCallHandler {
        void onSuccess();
        void onFailure(String message);
    }
}
