package com.worthybitbuilders.squadsense.viewmodels;

import androidx.annotation.NonNull;
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
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;
import com.worthybitbuilders.squadsense.services.ProjectService;
import com.worthybitbuilders.squadsense.services.RetrofitServices;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // for sorting features
    private SortState sortState = null;
    private int sortingColumnPosition = -1;
    public enum SortState {
        ASCENDING,
        DESCENDING
    }

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

    public SortState getSortState() {
        return sortState;
    }

    public int getSortingColumnPosition() {
        return sortingColumnPosition;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getBoardId() {
        return boardId;
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
            List<BoardBaseItemModel> newItemRow = new ArrayList<>(itemRow);
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

        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<List<String>> call = projectService.addNewColumnToRemote(userId, projectId, boardId, new NewColumnRequestModel(newColumnModel, itemModels));
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
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
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                throw new NotImplementedError();
            }
        });
    }

    public void deleteColumn(int columnPosition, ApiCallHandler handler) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<Void> call = projectService.deleteAColumn(userId, projectId, boardId, columnPosition);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    mColumnHeaderModelList.remove(columnPosition);
                    for (int i = 0; i < mCellModelList.size(); i++) {
                        mCellModelList.get(i).remove(columnPosition);
                    }

                    mColumnLiveData.setValue(mColumnHeaderModelList);
                    mCellLiveData.setValue(mCellModelList);
                    handler.onSuccess();
                } else handler.onFailure(response.message());
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                handler.onFailure(t.getMessage());
            }
        });
    };

    /**
     * there are 2 parts that can be updated: title and description
     * @param isChangingDescription: true if updating description, otherwise it's updating title
     */
    public void updateColumn(int columnPosition, String newContent, Boolean isChangingDescription, ApiCallHandler handler) throws JSONException {
        JSONObject data = new JSONObject();
        if (isChangingDescription) data.put("description", newContent);
        else data.put("title", newContent) ;

        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<Void> call = projectService.updateAColumn(userId, projectId, boardId, columnPosition, data);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    if (isChangingDescription)
                        mColumnHeaderModelList.get(columnPosition).setDescription(newContent);
                    else mColumnHeaderModelList.get(columnPosition).setTitle(newContent);

                    mColumnLiveData.setValue(mColumnHeaderModelList);
                    handler.onSuccess();
                } else handler.onFailure(response.message());
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                handler.onFailure(t.getMessage());
            }
        });
    }

    public Call<Void> deleteRow(int rowPosition) {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        return projectService.deleteARow(userId, projectId, boardId, rowPosition);
    }

    public void createNewRow(String title) {
        BoardRowHeaderModel newRowHeaderModel = new BoardRowHeaderModel(title, false);
        int columnPosition = mRowHeaderModelList.size() - 1;

        List<BoardBaseItemModel> newRowItems = new ArrayList<>();

        // the size() - 1 is to remove the empty "+ New Column"
        // we then later add it on the call "addNewRowToRemote" if "isSuccessful" below
        // because we are not uploading the "+ New Column" cell type
        for (int i = 0; i < mColumnHeaderModelList.size() - 1; i++) {
            newRowItems.add(BoardItemFactory.createNewItem(mColumnHeaderModelList.get(i).getColumnType()));
        }

        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        Call<List<String>> call = projectService.addNewRowToRemote(userId, projectId, boardId, new NewRowRequestModel(newRowHeaderModel, newRowItems));
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mRowHeaderModelList.add(columnPosition, newRowHeaderModel);
                    // set the _id that is returned from server
                    for (int i = 0; i < newRowItems.size(); i++) {
                        newRowItems.get(i).set_id(response.body().get(i));
                    }

                    // and also add a "+ New Column" cell because we cut it off
                    // because the reason above
                    newRowItems.add(new BoardEmptyItemModel());
                    mCellModelList.add(newRowItems);
                    mRowLiveData.setValue(mRowHeaderModelList);
                    mCellLiveData.setValue(mCellModelList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                throw new NotImplementedError();
            }
        });
    }

    /**
     * This function DOESN'T update the board
     * Use it along with boardAdapter.changeCellItem or pass it into the function
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

    public void sortColumn(int columnPosition, SortState sortState, TableViewAdapter adapter) {
        // if chosen column is sorted and user choose the same option
        // reset the board to default
        if (sortingColumnPosition == columnPosition && sortState == this.sortState) {
            sortingColumnPosition = -1;
            adapter.setAllItems(
                    mColumnHeaderModelList,
                    mRowHeaderModelList,
                    mCellModelList
            );
            adapter.notifyDataSetChanged();
            return;
        }

        // get the corresponding between row and cells
        List<BoardBaseItemModel> columnCells = new ArrayList<>();
        for (int i = 0; i < mCellModelList.size(); i++) {
            columnCells.add(mCellModelList.get(i).get(columnPosition));
        }
        Map<Integer, String> compareMap = new HashMap<>();
        for (int i = 0; i < columnCells.size(); i++) {
            compareMap.put(i, columnCells.get(i).get_id());
        }

        List<List<BoardBaseItemModel>> sortingCells = new ArrayList<>();
        for (int i = 0; i < mCellModelList.size(); i++) sortingCells.add(new ArrayList<>(mCellModelList.get(i)));

        if (sortState == SortState.ASCENDING) {
            Collections.sort(sortingCells, new Comparator<List<BoardBaseItemModel>>() {
                @Override
                public int compare(List<BoardBaseItemModel> list1, List<BoardBaseItemModel> list2) {
                    return list1.get(columnPosition).getContent().compareTo(list2.get(columnPosition).getContent());
                }
            });
        } else {
            Collections.sort(sortingCells, new Comparator<List<BoardBaseItemModel>>() {
                @Override
                public int compare(List<BoardBaseItemModel> list1, List<BoardBaseItemModel> list2) {
                    return list2.get(columnPosition).getContent().compareTo(list1.get(columnPosition).getContent());
                }
            });
        }

        List<BoardRowHeaderModel> sortingRow = new ArrayList<>();
        for (int i = 0; i < sortingCells.size(); i++) {
            for (Map.Entry<Integer, String> entry : compareMap.entrySet()) {
                if (entry.getValue().equals(sortingCells.get(i).get(columnPosition).get_id())) {
                    sortingRow.add(mRowHeaderModelList.get(entry.getKey()));
                    break;
                }
            }
        }

        this.sortingColumnPosition = columnPosition;
        this.sortState = sortState;

        // remove the "new column" header and cells
        List<BoardColumnHeaderModel> sortingColumn = new ArrayList<>(mColumnHeaderModelList);
        sortingColumn.remove(sortingColumn.size() - 1);
        for (int i = 0; i < sortingCells.size(); i++) {
            sortingCells.get(i).remove(sortingCells.get(i).size() - 1);
        }

        adapter.setAllItems(
                sortingColumn,
                sortingRow,
                sortingCells
        );
        adapter.notifyDataSetChanged();
    }

    public interface ApiCallHandler {
        void onSuccess();
        void onFailure(String message);
    }
}