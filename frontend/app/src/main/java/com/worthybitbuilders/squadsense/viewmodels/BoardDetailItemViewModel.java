package com.worthybitbuilders.squadsense.viewmodels;
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
import com.worthybitbuilders.squadsense.models.board_models.BoardUpdateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;
import com.worthybitbuilders.squadsense.services.ProjectService;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardDetailItemViewModel extends ViewModel {
    private BoardDetailItemModel data;
    private int rowPosition;
    private final MutableLiveData<BoardDetailItemModel> itemsLiveData = new MutableLiveData<>(null);
    private List<UpdateTask> updateTasks;
    private MutableLiveData<List<UpdateTask>> updateTasksLiveData = new MutableLiveData<>(null);
    private ProjectService projectService = RetrofitServices.getProjectService();

    /**
     * @param rowPosition is the position according to the board,
     *                    we need it to update the exact cell on the remote
     */
    public BoardDetailItemViewModel(int rowPosition) {
        this.rowPosition = rowPosition;
    }
    public int getRowPosition() {
        return rowPosition;
    }
    public MutableLiveData<BoardDetailItemModel> getItemsLiveData() {
        return itemsLiveData;
    }
    public MutableLiveData<List<UpdateTask>> getUpdateTasksLiveData() {
        return updateTasksLiveData;
    }

    public void getDataFromRemote(String projectId, String boardId, int rowPosition, GetDataHandlers handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        projectService.getCellsInARow(userId, projectId, boardId, rowPosition).enqueue(new Callback<BoardDetailItemModel>() {
            @Override
            public void onResponse(Call<BoardDetailItemModel> call, Response<BoardDetailItemModel> response) {
                if (response.isSuccessful()) {
                    data = response.body();
                    itemsLiveData.setValue(data);
                    handlers.onSuccess();
                } else {
                    handlers.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<BoardDetailItemModel> call, Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }

    public void getUpdateTasksByCellId(String projectId, String boardId, String cellId, GetDataHandlers handlers) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        projectService.getAllUpdateTasksOfACell(userId, projectId, boardId, cellId).enqueue(new Callback<List<UpdateTask>>() {
            @Override
            public void onResponse(Call<List<UpdateTask>> call, Response<List<UpdateTask>> response) {
                if (response.isSuccessful()) {
                    updateTasks = response.body();
                    updateTasksLiveData.setValue(updateTasks);
                    handlers.onSuccess();
                } else {
                    handlers.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<List<UpdateTask>> call, Throwable t) {
                handlers.onFailure(t.getMessage());
            }
        });
    }

    /**
     * This function DOESN'T update the board
     * Use it along with AN ADAPTER or pass it into the function
     */
    public Call<Void> updateACell(BoardBaseItemModel cellModel, String projectId, String boardId) {
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

    public interface GetDataHandlers {
        void onSuccess();
        void onFailure(String message);
    }
}
