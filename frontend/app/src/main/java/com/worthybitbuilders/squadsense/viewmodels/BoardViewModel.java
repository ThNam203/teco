package com.worthybitbuilders.squadsense.viewmodels;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.worthybitbuilders.squadsense.adapters.TableViewAdapter;
import com.worthybitbuilders.squadsense.factory.BoardItemFactory;
import com.worthybitbuilders.squadsense.models.NewColumnRequestModel;
import com.worthybitbuilders.squadsense.models.NewRowRequestModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardBaseItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardCheckboxItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardColumnHeaderModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardContentModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardDateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardEmptyItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardNumberItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardRowHeaderModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardStatusItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTextItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTimelineItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUpdateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;
import com.worthybitbuilders.squadsense.services.ProjectService;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

import kotlin.NotImplementedError;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardViewModel extends ViewModel {
    private String boardTitle;
    private String projectId;
    private String boardId;
    private List<BoardColumnHeaderModel> mColumnHeaderModelList;
    private List<BoardRowHeaderModel> mRowHeaderModelList;
    private List<List<BoardBaseItemModel>> mCellModelList;
    private final MutableLiveData<List<BoardColumnHeaderModel>> mColumnLiveData = new MutableLiveData<>(null);
    private final MutableLiveData<List<BoardRowHeaderModel>> mRowLiveData = new MutableLiveData<>(null);
    private final MutableLiveData<List<List<BoardBaseItemModel>>> mCellLiveData = new MutableLiveData<>(null);
    private final MutableLiveData<String> boardTitleLiveData = new MutableLiveData<>(null);

    private final ProjectService projectService = RetrofitServices.getProjectService();

    public BoardViewModel() {}

    public int getCellItemViewType(int columnPosition) {
        try {
            return mColumnHeaderModelList.get(columnPosition).getColumnType().getKey();
        } catch (IndexOutOfBoundsException e) {
            return BoardColumnHeaderModel.ColumnType.NewColumn.getKey();
        } catch (NullPointerException e) {
            throw new NotImplementedError();
        }
    }

    public String getBoardTitle() {
        if (boardTitle == null || boardTitle.isEmpty()) return "Unknown title";
        return boardTitle;
    }

    public void setBoardTitle(String boardTitle) {
        this.boardTitle = boardTitle;
        boardTitleLiveData.setValue(boardTitle);
    }

    public MutableLiveData<String> getBoardTitleLiveData() {
        return boardTitleLiveData;
    }

    public MutableLiveData<List<BoardColumnHeaderModel>> getColumnLiveData() {
        return mColumnLiveData;
    }

    public MutableLiveData<List<BoardRowHeaderModel>> getRowLiveData() {
        return mRowLiveData;
    }

    public MutableLiveData<List<List<BoardBaseItemModel>>> getCellLiveData() {
        return mCellLiveData;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public void setBoardContent(BoardContentModel content, String projectId, TableViewAdapter tableViewAdapter) {
        setBoardTitle(content.getBoardTitle());
        setProjectId(projectId);
        setBoardId(content.get_id());
        generateDataForBoard(content);
        tableViewAdapter.setAllItems(
                mColumnHeaderModelList,
                mRowHeaderModelList,
                mCellModelList
        );
        tableViewAdapter.notifyDataSetChanged();
    }

    // The trigger part is "BoardContentModel"
    private void generateDataForBoard(BoardContentModel contentModel) {
        mColumnHeaderModelList = createColumnHeaderModelList(contentModel);
        mRowHeaderModelList = createRowHeaderList(contentModel);
        mCellModelList = createCellModelList(contentModel);
    }

    private List<BoardRowHeaderModel> createRowHeaderList(BoardContentModel contentModel) {
        List<BoardRowHeaderModel> data = new ArrayList<>();
        contentModel.getRowCells().forEach(title -> {
            data.add(new BoardRowHeaderModel(title, false));
        });
        data.add(new BoardRowHeaderModel("+ New Row", true));

        return data;
    }

    private List<List<BoardBaseItemModel>> createCellModelList(BoardContentModel contentModel) {
        List<List<BoardBaseItemModel>> data = new ArrayList<>();

        contentModel.getCells().forEach(itemRow -> {
            List<BoardBaseItemModel> newItemRow = new ArrayList<>();
            itemRow.forEach(item -> newItemRow.add(item));
            // add the empty cells for "+ New Column"
            newItemRow.add(new BoardEmptyItemModel());
            data.add(newItemRow);
        });

        return data;
    }

    private List<BoardColumnHeaderModel> createColumnHeaderModelList(BoardContentModel contentModel) {
        List<BoardColumnHeaderModel> columnRow = new ArrayList<>(contentModel.getColumnCells());
        columnRow.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.NewColumn, "+ New Column"));
        return columnRow;
    }

    public void createNewColumn(BoardColumnHeaderModel.ColumnType columnType) {
        // create the corresponding items
        BoardColumnHeaderModel newColumnModel = new BoardColumnHeaderModel(columnType, columnType.getName());
        List<BoardBaseItemModel> itemModels = new ArrayList<>();

        int columnPosition = mColumnHeaderModelList.size() - 1;
        for (int i = 0; i < mRowHeaderModelList.size(); i++) {
            BoardBaseItemModel newModel = BoardItemFactory.createNewItem(newColumnModel.getColumnType());
            itemModels.add(newModel);
        }

        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USERID);
        Call<List<String>> call = projectService.addNewColumnToRemote(userId, projectId, boardId, new NewColumnRequestModel(newColumnModel, itemModels));
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mColumnHeaderModelList.add(columnPosition, newColumnModel);
                    for (int i = 0; i < mCellModelList.size(); i++) {
                        // set the _id that is returned from server
                        itemModels.get(i).set_id(response.body().get(i));
                        mCellModelList.get(i).add(columnPosition, itemModels.get(i));
                    }

                    mColumnLiveData.setValue(mColumnHeaderModelList);
                    mCellLiveData.setValue(mCellModelList);
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                throw new NotImplementedError();
            }
        });
    }

    public void createNewRow(String title) {
        BoardRowHeaderModel newRowHeaderModel = new BoardRowHeaderModel(title, false);
        int columnPosition = mRowHeaderModelList.size() - 1;

        List<BoardBaseItemModel> newRowItems = new ArrayList<>();

        // the size() - 1 is to remove the empty "+ New Column"
        for (int i = 0; i < mColumnHeaderModelList.size() - 1; i++) {
            newRowItems.add(BoardItemFactory.createNewItem(mColumnHeaderModelList.get(i).getColumnType()));
        }

        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USERID);
        Call<List<String>> call = projectService.addNewRowToRemote(userId, projectId, boardId, new NewRowRequestModel(newRowHeaderModel, newRowItems));
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mRowHeaderModelList.add(columnPosition, newRowHeaderModel);

                    // set the _id that is returned from server
                    for (int i = 0; i < newRowItems.size(); i++) {
                        newRowItems.get(i).set_id(response.body().get(i));
                    }
                    mCellModelList.add(newRowItems);
                    mRowLiveData.setValue(mRowHeaderModelList);
                    mCellLiveData.setValue(mCellModelList);
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                throw new NotImplementedError();
            }
        });
    }

    /**
     * This function DOESN'T update the board
     * Use it along with boardAdapter.changeCellItem or pass it into the function
     */
    public Call<Void> updateACell(BoardBaseItemModel cellModel) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USERID);
        switch (cellModel.getCellType()) {
            case "CellStatus":
                return projectService.updateCellToRemote(userId, projectId, boardId, cellModel.get_id(), (BoardStatusItemModel) cellModel);
            case "CellUpdate":
                return projectService.updateCellToRemote(userId, projectId, boardId, cellModel.get_id(), (BoardUpdateItemModel) cellModel);
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
}