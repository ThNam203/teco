package com.worthybitbuilders.squadsense.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.adapters.BoardItemMemberAdapter;
import com.worthybitbuilders.squadsense.adapters.BoardItemOwnerAdapter;
import com.worthybitbuilders.squadsense.adapters.EditBoardsAdapter;
import com.worthybitbuilders.squadsense.adapters.StatusContentsAdapter;
import com.worthybitbuilders.squadsense.adapters.StatusEditItemAdapter;
import com.worthybitbuilders.squadsense.adapters.TableViewAdapter;
import com.worthybitbuilders.squadsense.adapters.activityLogFilterAdapter.FilterTypeAdapter;
import com.worthybitbuilders.squadsense.adapters.filterBoardAdapter.BoardFilterAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityProjectBinding;
import com.worthybitbuilders.squadsense.databinding.BoardAddItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardAddNewRowPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardColumnDescriptionPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardColumnRenamePopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardDateItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardEditBoardsViewBinding;
import com.worthybitbuilders.squadsense.databinding.BoardNumberItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardOwnerItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardStatusEditNewItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardStatusEditViewBinding;
import com.worthybitbuilders.squadsense.databinding.BoardStatusItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardTextItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardTimelineItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.ColumnMoreOptionsBinding;
import com.worthybitbuilders.squadsense.databinding.PopupFilterActivityLogBinding;
import com.worthybitbuilders.squadsense.databinding.PopupFilterBoardBinding;
import com.worthybitbuilders.squadsense.databinding.PopupRenameBinding;
import com.worthybitbuilders.squadsense.databinding.ProjectMoreOptionsBinding;
import com.worthybitbuilders.squadsense.factory.ProjectActivityViewModelFactory;
import com.worthybitbuilders.squadsense.models.FilterModel;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardBaseItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardCheckboxItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardColumnHeaderModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardContentModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardDateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardMapItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardNumberItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardRowHeaderModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardStatusItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTextItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTimelineItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUpdateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;
import com.worthybitbuilders.squadsense.models.board_models.ProjectModel;
import com.worthybitbuilders.squadsense.utils.ActivityUtils;
import com.worthybitbuilders.squadsense.utils.CustomUtils;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.BoardViewModel;
import com.worthybitbuilders.squadsense.viewmodels.ProjectActivityViewModel;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

import org.json.JSONException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectActivity extends AppCompatActivity {
    private TableViewAdapter boardAdapter;
    private ProjectActivityViewModel projectActivityViewModel;
    // This differs from "projectActivityViewModel", this holds logic for only TableView
    private BoardViewModel boardViewModel;
    private ActivityProjectBinding activityBinding;
    private UserViewModel userViewModel;
    private boolean isNewProjectCreateRequest = false;
    private final List<List<String>> listSelectedCollection = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        activityBinding = ActivityProjectBinding.inflate(getLayoutInflater());
        setContentView(activityBinding.getRoot());
        activityBinding.tableView.setShowCornerView(true);
        activityBinding.btnShowTables.setOnClickListener(view -> showTables());
        activityBinding.btnFilter.setOnClickListener(view -> showPopupFilter());
        activityBinding.btnNewBoardOnEmpty.setOnClickListener(view -> showTables());

        Intent intent = getIntent();
        ProjectActivityViewModelFactory factory = new ProjectActivityViewModelFactory(intent.getStringExtra("projectId"));
        projectActivityViewModel = new ViewModelProvider(this, factory).get(ProjectActivityViewModel.class);
        projectActivityViewModel.setInitialChosenBoardPosition(intent.getIntExtra("boardPosition", 0));
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        boardViewModel = new ViewModelProvider(this).get(BoardViewModel.class);

        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        boardAdapter = new TableViewAdapter(this, boardViewModel, new TableViewAdapter.OnClickHandlers() {
            @Override
            public void OnMapItemClick(BoardMapItemModel itemModel, String columnTitle, int columnPos, int rowPos) {
                if(isTaskDone(rowPos)) return;
                if(!isAnOwnerOfRow(userId, rowPos)) return;
                Intent mapIntent = new Intent(ProjectActivity.this, MapActivity.class);
                String itemJson = new Gson().toJson(itemModel);
                mapIntent.putExtra("itemModel", itemJson);
                mapIntent.putExtra("projectId", projectActivityViewModel.getProjectId());
                mapIntent.putExtra("boardId", boardViewModel.getBoardId());
                mapIntent.putExtra("cellId", itemModel.get_id());
                startActivity(mapIntent);
            }

            @Override
            public void OnTimelineItemClick(BoardTimelineItemModel itemModel, String columnTitle, int columnPos, int rowPos) {
                if(isTaskDone(rowPos)) return;
                if(!isAnOwnerOfRow(userId, rowPos)) return;
                showTimelineItemPopup(itemModel, columnTitle, columnPos, rowPos);
            }

            @Override
            public void OnDateItemClick(BoardDateItemModel itemModel, String columnTitle, int columnPos, int rowPos) {
                if(isTaskDone(rowPos)) return;
                if(!isAnOwnerOfRow(userId, rowPos)) return;
                showDateItemPopup(itemModel, columnTitle, columnPos, rowPos);
            }

            @Override
            public void onCheckboxItemClick(BoardCheckboxItemModel itemModel, int columnPos, int rowPos) {
                if(isTaskDone(rowPos)) return;
                if(!isAnOwnerOfRow(userId, rowPos)) return;
                onCheckboxItemClicked(itemModel, columnPos, rowPos);
            }

            @Override
            public void onUpdateItemClick(BoardUpdateItemModel itemModel, int rowPosition, String rowTitle, String columnTitle) {
                if(!isAnOwnerOfRow(userId, rowPosition)) return;
                Intent updateIntent = new Intent(ProjectActivity.this, BoardItemDetailActivity.class);
                updateIntent.putExtra("projectId", projectActivityViewModel.getProjectId());
                updateIntent.putExtra("boardId", boardViewModel.getBoardId());
                updateIntent.putExtra("rowPosition", rowPosition);
                updateIntent.putExtra("rowTitle", rowTitle);
                updateIntent.putExtra("projectTitle", projectActivityViewModel.getProjectModel().getTitle());
                updateIntent.putExtra("boardTitle", boardViewModel.getBoardTitle());
                updateIntent.putExtra("updateCellId", itemModel.get_id());
                updateIntent.putExtra("updateCellTitle", columnTitle);
                updateIntent.putExtra("isFromUpdateColumn", true);
                updateIntent.putExtra("isDone", boardViewModel.getmRowHeaderModelList().get(rowPosition).isDone());
                startActivity(updateIntent);
            }

            @Override
            public void onNumberItemClick(BoardNumberItemModel itemModel, String columnTitle, int columnPos, int rowPos) {
                if(isTaskDone(rowPos)) return;
                if(!isAnOwnerOfRow(userId, rowPos)) return;
                showNumberItemPopup(itemModel, columnTitle, columnPos, rowPos);
            }

            @Override
            public void onNewColumnHeaderClick() {
                String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
                if(projectActivityViewModel.getProjectModel().getCreatorId().equals(userId) || projectActivityViewModel.getProjectModel().getAdminIds().contains(userId))
                    showAddBoardItemPopup();
                else ToastUtils.showToastError(ProjectActivity.this, "You don't have permission to add new collumn", Toast.LENGTH_SHORT);
            }

            @Override
            public void onNewRowHeaderClick() {
                String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
                if(projectActivityViewModel.getProjectModel().getCreatorId().equals(userId) || projectActivityViewModel.getProjectModel().getAdminIds().contains(userId))
                    showNewRowPopup();
                else ToastUtils.showToastError(ProjectActivity.this, "You don't have permission to add new row", Toast.LENGTH_SHORT);
            }

            @Override
            public void onRowHeaderClick(int rowPosition, String rowTitle) {
                if(!isAnOwnerOfRow(userId, rowPosition)) return;
                Intent showRowIntent = new Intent(ProjectActivity.this, BoardItemDetailActivity.class);
                showRowIntent.putExtra("projectId", projectActivityViewModel.getProjectId());
                showRowIntent.putExtra("boardId", boardViewModel.getBoardId());
                showRowIntent.putExtra("projectTitle", projectActivityViewModel.getProjectModel().getTitle());
                showRowIntent.putExtra("boardTitle", boardViewModel.getBoardTitle());
                showRowIntent.putExtra("rowPosition", rowPosition);
                showRowIntent.putExtra("rowTitle", rowTitle);
                showRowIntent.putExtra("creatorId", projectActivityViewModel.getProjectModel().getCreatorId());
                showRowIntent.putExtra("isDone", boardViewModel.getmRowHeaderModelList().get(rowPosition).isDone());

                Bundle bundle = new Bundle();
                bundle.putStringArrayList("adminId", (ArrayList<String>) projectActivityViewModel.getProjectModel().getAdminIds());
                showRowIntent.putExtras(bundle);

                startActivity(showRowIntent);
            }

            @Override
            public void onColumnHeaderClick(BoardColumnHeaderModel headerModel, int columnPosition, View anchor) {
                showColumnHeaderOptions(headerModel, columnPosition, anchor);
            }

            @Override
            public void onTextItemClick(BoardTextItemModel itemModel, String columnTitle, int columnPos, int rowPos) {
                if(isTaskDone(rowPos)) return;
                if(!isAnOwnerOfRow(userId, rowPos)) return;
                showTextItemPopup(itemModel, columnTitle, columnPos, rowPos);
            }

            @Override
            public void onUserItemClick(BoardUserItemModel userItemModel, String columnTitle, int columnPos, int rowPos) {
                if(isTaskDone(rowPos)) return;
                if(!isAnOwnerOfRow(userId, rowPos)) return;
                showOwnerPopup(userItemModel, columnTitle, columnPos, rowPos);
            }

            @Override
            public void onStatusItemClick(BoardStatusItemModel itemModel, String columnTitle, int columnPos, int rowPos) {
                if(isTaskDone(rowPos)) return;
                if(!isAnOwnerOfRow(userId, rowPos)) return;
                showTaskStatusPopup(itemModel, columnTitle, columnPos, rowPos);
            }
        });
        activityBinding.tableView.setAdapter(boardAdapter);

//        projectActivityViewModel.getProjectModel().setChosenPosition(intent.getIntExtra("boardPosition", 0));sdf
        projectActivityViewModel.getProjectModelLiveData().observe(this, projectModel -> {
            if (projectModel == null) return;
            // set cells content, pass the adapter to let them call the set item

            activityBinding.tvProjectTitle.setText(projectModel.getTitle());
            if (projectModel.getBoards().size() == 0) {
                activityBinding.emptyBoardNotification.setVisibility(View.VISIBLE);
                return;
            } else activityBinding.emptyBoardNotification.setVisibility(View.GONE);

            // the case is if user remove all board and add one, but the chosenPosition is bigger than 1
            if (projectModel.getChosenPosition() >= projectModel.getBoards().size()) projectModel.setChosenPosition(0);

            BoardContentModel content = projectModel.getBoards().get(projectModel.getChosenPosition());
            boardViewModel.setBoardContent(content, projectModel.get_id(), boardAdapter);
            // set board title for "more table" drop down
            activityBinding.btnShowTables.setText(
                    projectModel.getBoards()
                            .get(projectModel.getChosenPosition())
                            .getBoardTitle());
        });

        activityBinding.btnMoreOptions.setOnClickListener(this::showProjectOptions);
        activityBinding.btnBack.setOnClickListener((view) -> onBackPressed());
        createNewProjectIfRequest();
    }

    private boolean isTaskDone(int rowPosition)
    {
        boolean isDone = boardViewModel.getmRowHeaderModelList().get(rowPosition).isDone();
        if(isDone) {
            ToastUtils.showToastError(ProjectActivity.this, "This task is already completed and cannot be edited", Toast.LENGTH_SHORT);
            return true;
        }
        else return false;
    }



    private void showColumnHeaderOptions(BoardColumnHeaderModel headerModel, int columnPosition, View anchor) {
        ColumnMoreOptionsBinding binding = ColumnMoreOptionsBinding.inflate(getLayoutInflater());
        PopupWindow popupWindow = new PopupWindow(binding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setElevation(50);

        if (headerModel.getColumnType() == BoardColumnHeaderModel.ColumnType.Update) {
            binding.btnSortAsc.setVisibility(View.GONE);
            binding.btnSortDesc.setVisibility(View.GONE);
        } else if (boardViewModel.getSortingColumnPosition() == columnPosition) {
            if (boardViewModel.getSortState() == BoardViewModel.SortState.ASCENDING)
                DrawableCompat.setTint(binding.btnSortAscContainer.getBackground(), Color.parseColor("#8ecae6"));
            else DrawableCompat.setTint(binding.btnSortAscContainer.getBackground(), ContextCompat.getColor(ProjectActivity.this, R.color.transparent));

            if (boardViewModel.getSortState() == BoardViewModel.SortState.DESCENDING)
                DrawableCompat.setTint(binding.btnSortDescContainer.getBackground(), Color.parseColor("#8ecae6"));
            else DrawableCompat.setTint(binding.btnSortDescContainer.getBackground(), ContextCompat.getColor(ProjectActivity.this, R.color.transparent));
        }

        if (headerModel.getColumnType() == BoardColumnHeaderModel.ColumnType.Date ||
            headerModel.getColumnType() == BoardColumnHeaderModel.ColumnType.TimeLine) {
            binding.btnMarkAsDeadline.setVisibility(View.VISIBLE);
            int boardPosition = projectActivityViewModel.getProjectModel().getChosenPosition();
            BoardContentModel boardContentModel = projectActivityViewModel.getProjectModel().getBoards().get(boardPosition);
            if (boardContentModel.getDeadlineColumnIndex() != null &&
                boardContentModel.getDeadlineColumnIndex() != -1 &&
                boardContentModel.getDeadlineColumnIndex() == columnPosition) {
                binding.btnMarkAsDeadline.setText("Remove deadline");
                binding.btnMarkAsDeadline.setOnClickListener((view) -> {
                    try {
                        projectActivityViewModel.updateBoardDeadlineColumnIndex(boardPosition, -1, new ProjectActivityViewModel.ApiCallHandlers() {
                            @Override
                            public void onSuccess() {
                                // update the cells in column
                                boardViewModel.setDeadlineColumnIndex(-1);
                                for (int i = 0; i < boardContentModel.getRowCells().size(); i++)
                                    boardAdapter.changeCellItem(columnPosition, i, boardViewModel.getmCellModelList().get(i).get(columnPosition));
                                popupWindow.dismiss();
                            }

                            @Override
                            public void onFailure(String message) {
                                ToastUtils.showToastError(ProjectActivity.this, message, Toast.LENGTH_SHORT);
                                popupWindow.dismiss();
                            }
                        });
                    } catch (JSONException e) {
                        ToastUtils.showToastError(ProjectActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT);
                        popupWindow.dismiss();
                    }
                });
            } else {
                binding.btnMarkAsDeadline.setOnClickListener((view) -> {
                    try {
                        projectActivityViewModel.updateBoardDeadlineColumnIndex(boardPosition, columnPosition, new ProjectActivityViewModel.ApiCallHandlers() {
                            @Override
                            public void onSuccess() {
                                // update the cells in column
                                boardViewModel.setDeadlineColumnIndex(columnPosition);
                                for (int i = 0; i < boardContentModel.getRowCells().size(); i++)
                                    boardAdapter.changeCellItem(columnPosition, i, boardViewModel.getmCellModelList().get(i).get(columnPosition));
                                popupWindow.dismiss();
                            }

                            @Override
                            public void onFailure(String message) {
                                ToastUtils.showToastError(ProjectActivity.this, message, Toast.LENGTH_SHORT);
                                popupWindow.dismiss();
                            }
                        });
                    } catch (JSONException e) {
                        ToastUtils.showToastError(ProjectActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT);
                        popupWindow.dismiss();
                    }
                });
            }
        }

        binding.btnSortAsc.setOnClickListener(view -> {
            boardViewModel.sortColumn(columnPosition, BoardViewModel.SortState.ASCENDING, boardAdapter);
            // if we sort column then we remove the filter criteria to not click each other
            for (int i = 0; i < listSelectedCollection.size(); i++) listSelectedCollection.get(i).clear();
            popupWindow.dismiss();
        });

        binding.btnSortDesc.setOnClickListener(view -> {
            boardViewModel.sortColumn(columnPosition, BoardViewModel.SortState.DESCENDING, boardAdapter);
            // if we sort column then we remove the filter criteria to not click each other
            for (int i = 0; i < listSelectedCollection.size(); i++) listSelectedCollection.get(i).clear();
            popupWindow.dismiss();
        });

        binding.btnDescription.setOnClickListener(view -> {
            popupWindow.dismiss();
            showColumnDescription(headerModel, columnPosition);
        });

        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        if (Objects.equals(userId, projectActivityViewModel.getProjectModel().getCreatorId())) {
            binding.btnRemove.setOnClickListener(view -> {
                popupWindow.dismiss();
                showConfirmDeleteColumn(headerModel, columnPosition);
            });

            binding.btnRename.setOnClickListener(view -> {
                popupWindow.dismiss();
                showRenameColumnDialog(headerModel, columnPosition);
            });
        } else {
            binding.btnRemove.setVisibility(View.GONE);
            binding.btnRename.setVisibility(View.GONE);
        }

        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(anchor, 0, 0);
    }

    private void showRenameColumnDialog(BoardColumnHeaderModel itemModel, int columnPosition) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardColumnRenamePopupBinding binding = BoardColumnRenamePopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        binding.btnClosePopup.setOnClickListener((view) -> dialog.dismiss());
        binding.etDescription.setText(itemModel.getTitle());
        binding.btnClearDescription.setOnClickListener((view) -> binding.etDescription.setText(""));
        binding.btnSaveTextItem.setOnClickListener(view -> {
            Dialog loadingDialog = DialogUtils.GetLoadingDialog(ProjectActivity.this);
            loadingDialog.show();
            String newName = binding.etDescription.getText().toString();
            if (newName.isEmpty()) {
                ToastUtils.showToastError(ProjectActivity.this, "Unable to save empty name", Toast.LENGTH_SHORT);
                return;
            }
            try {
                boardViewModel.updateColumn(columnPosition, newName, false, new BoardViewModel.ApiCallHandler() {
                    @Override
                    public void onSuccess() {
                        dialog.dismiss();
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtils.showToastError(ProjectActivity.this, "Unable to save new name, please try again", Toast.LENGTH_SHORT);
                        loadingDialog.dismiss();
                    }
                });
            } catch (JSONException e) {
                ToastUtils.showToastError(ProjectActivity.this, "Unable to save new name, please try again", Toast.LENGTH_SHORT);
                loadingDialog.dismiss();
            }
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void showProjectOptions(View anchor) {
        ProjectMoreOptionsBinding binding = ProjectMoreOptionsBinding.inflate(getLayoutInflater());
        PopupWindow popupWindow = new PopupWindow(binding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setElevation(50);

        //change view for different roles in project like view for user, admin, creator
        String creatorId = projectActivityViewModel.getProjectModel().getCreatorId();
        List<String> adminIds = projectActivityViewModel.getProjectModel().getAdminIds();
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        if(creatorId.equals(userId))
            showProjectOptionFor(Role.CREATOR, binding);
        else if(adminIds.contains(userId))
            showProjectOptionFor(Role.ADMIN, binding);
        else
            showProjectOptionFor(Role.MEMBER, binding);

        binding.btnLeaveProject.setOnClickListener((view) -> {
            projectActivityViewModel.leaveProject(new ProjectActivityViewModel.ApiCallHandlers() {
                @Override
                public void onSuccess() {
                    finish();
                }

                @Override
                public void onFailure(String message) {
                    ToastUtils.showToastError(ProjectActivity.this, message, Toast.LENGTH_SHORT);
                }
            });
        });


        binding.btnShowMember.setOnClickListener(view -> ActivityUtils.switchToActivity(ProjectActivity.this, MemberActivity.class));

        binding.btnRenameProject.setOnClickListener(view -> showRenamePopup());

        binding.btnRequestAdmin.setOnClickListener(view -> {
            String projectId = projectActivityViewModel.getProjectId();
            projectActivityViewModel.requestAdmin(projectId, new ProjectActivityViewModel.ApiCallHandlers() {
                @Override
                public void onSuccess() {
                    ToastUtils.showToastSuccess(ProjectActivity.this, "Your request was sent to admins of this project", Toast.LENGTH_SHORT);
                    popupWindow.dismiss();
                }

                @Override
                public void onFailure(String message) {
                    ToastUtils.showToastError(ProjectActivity.this, message, Toast.LENGTH_SHORT);
                }
            });
        });

        binding.btnActivityLog.setOnClickListener(view -> {
            Intent activityLogIntent = new Intent(ProjectActivity.this, ProjectActivityLogActivity.class);
            activityLogIntent.putExtra("projectId", projectActivityViewModel.getProjectId());
            startActivity(activityLogIntent);
        });

        binding.btnDeleteProject.setOnClickListener(view -> {
            String titleConfirmDialog = "Delete";
            String contentConfirmDialog = "Do you want to delete this project ?";
            DialogUtils.showConfirmDialogDelete(ProjectActivity.this, titleConfirmDialog, contentConfirmDialog, new DialogUtils.ConfirmAction() {
                @Override
                public void onAcceptToDo(Dialog thisDialog) {
                    thisDialog.dismiss();
                    Dialog loadingDialog = DialogUtils.GetLoadingDialog(ProjectActivity.this);
                    loadingDialog.show();
                    String projectId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.CURRENT_PROJECT_ID);
                    projectActivityViewModel.deleteProject(projectId, new ProjectActivityViewModel.ApiCallHandlers() {
                        @Override
                        public void onSuccess() {
                            ToastUtils.showToastSuccess(ProjectActivity.this, "Project deleted", Toast.LENGTH_SHORT);
                            loadingDialog.dismiss();
                            ProjectActivity.this.onBackPressed();
                        }

                        @Override
                        public void onFailure(String message) {
                            ToastUtils.showToastError(ProjectActivity.this, "You are not allowed to delete this project", Toast.LENGTH_SHORT);
                            loadingDialog.dismiss();
                        }
                    });

                }

                @Override
                public void onCancel(Dialog thisDialog) {
                    thisDialog.dismiss();
                }
            });
        });

        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(anchor, 0, 0);
    }

    private enum Role{MEMBER, CREATOR, ADMIN}
    private void showProjectOptionFor(Role role, ProjectMoreOptionsBinding projectMoreOptionsBinding)
    {
        projectMoreOptionsBinding.btnRequestAdmin.setVisibility(View.VISIBLE);
        projectMoreOptionsBinding.btnLeaveProject.setVisibility(View.VISIBLE);
        projectMoreOptionsBinding.btnDeleteProject.setVisibility(View.VISIBLE);
        projectMoreOptionsBinding.btnRenameProject.setVisibility(View.VISIBLE);
        projectMoreOptionsBinding.btnShowMember.setVisibility(View.VISIBLE);
        projectMoreOptionsBinding.btnActivityLog.setVisibility(View.VISIBLE);

        switch (role)
        {
            case CREATOR:
                projectMoreOptionsBinding.btnLeaveProject.setVisibility(View.GONE);
                projectMoreOptionsBinding.btnRequestAdmin.setVisibility(View.GONE);
                break;
            case ADMIN:
                projectMoreOptionsBinding.btnRequestAdmin.setVisibility(View.GONE);
                projectMoreOptionsBinding.btnDeleteProject.setVisibility(View.GONE);
                projectMoreOptionsBinding.btnRenameProject.setVisibility(View.GONE);
                break;
            case MEMBER:
                projectMoreOptionsBinding.btnDeleteProject.setVisibility(View.GONE);
                projectMoreOptionsBinding.btnRenameProject.setVisibility(View.GONE);
                break;
        }
    }

    private void showPopupOwnerFor(Role role, BoardOwnerItemPopupBinding popupBinding)
    {
        popupBinding.rvMembers.setVisibility(View.VISIBLE);
        popupBinding.layoutRvOwers.setVisibility(View.VISIBLE);
        popupBinding.btnSave.setVisibility(View.VISIBLE);

        switch (role)
        {
            case CREATOR:
                break;
            case ADMIN:
                break;
            case MEMBER:
                popupBinding.rvMembers.setVisibility(View.GONE);
                popupBinding.btnSave.setVisibility(View.GONE);
                break;
        }
    }

    private void showRenamePopup()
    {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        PopupRenameBinding popupRenameBinding = PopupRenameBinding.inflate(getLayoutInflater());
        dialog.setContentView(popupRenameBinding.getRoot());

        popupRenameBinding.input.setText(projectActivityViewModel.getProjectModel().getTitle());
        popupRenameBinding.input.requestFocus();

        popupRenameBinding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProjectModel projectToUpdate = projectActivityViewModel.getProjectModel();
                projectToUpdate.setTitle(popupRenameBinding.input.getText().toString());
                projectActivityViewModel.updateProject(projectToUpdate, new ProjectActivityViewModel.ApiCallHandlers() {
                    @Override
                    public void onSuccess() {
                        activityBinding.tvProjectTitle.setText(popupRenameBinding.input.getText().toString());
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtils.showToastError(ProjectActivity.this, message, Toast.LENGTH_SHORT);
                    }
                });
            }
        });

        popupRenameBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }

    private void showColumnDescription(BoardColumnHeaderModel itemModel, int columnPos) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardColumnDescriptionPopupBinding binding = BoardColumnDescriptionPopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        binding.btnClosePopup.setOnClickListener((view) -> dialog.dismiss());
        binding.textItemTitle.setText(itemModel.getTitle());
        if (!itemModel.getDescription().isEmpty()) {
            binding.tvContent.setText(itemModel.getDescription());
            binding.etDescription.setText(itemModel.getDescription());
        }

        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        // if the user is the author, then make the description editable
        if (Objects.equals(projectActivityViewModel.getProjectModel().getCreatorId(), userId)) {
            binding.btnSaveTextItem.setVisibility(View.VISIBLE);
            binding.tvContent.setVisibility(View.GONE);
            binding.editDescriptionContainer.setVisibility(View.VISIBLE);
            binding.btnClearDescription.setOnClickListener((view) -> binding.etDescription.setText(""));

            binding.btnSaveTextItem.setOnClickListener(view -> {
                Dialog loadingDialog = DialogUtils.GetLoadingDialog(ProjectActivity.this);
                loadingDialog.show();
                String newDescription = binding.etDescription.getText().toString();
                try {
                    boardViewModel.updateColumn(columnPos, newDescription, true, new BoardViewModel.ApiCallHandler() {
                        @Override
                        public void onSuccess() {
                            dialog.dismiss();
                            loadingDialog.dismiss();
                        }

                        @Override
                        public void onFailure(String message) {
                            ToastUtils.showToastError(ProjectActivity.this, "Unable to save the description, please try again", Toast.LENGTH_SHORT);
                            loadingDialog.dismiss();
                        }
                    });
                } catch (JSONException e) {
                    ToastUtils.showToastError(ProjectActivity.this, "Unable to save the description, please try again", Toast.LENGTH_SHORT);
                    loadingDialog.dismiss();
                }
            });
        }

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void showConfirmDeleteColumn(BoardColumnHeaderModel headerModel, int columnPosition) {
        String titleConfirmDialog = String.format(Locale.US, "Delete column \"%s\"?", headerModel.getTitle());
        String contentConfirmDialog = "This column will be removed from the board";
        DialogUtils.showConfirmDialogDelete(this, titleConfirmDialog, contentConfirmDialog, new DialogUtils.ConfirmAction() {
            @Override
            public void onAcceptToDo(Dialog thisDialog) {
                boardViewModel.deleteColumn(columnPosition, new BoardViewModel.ApiCallHandler() {
                    @Override
                    public void onSuccess() {
                        projectActivityViewModel.getProjectModel().getBoards().get(
                                projectActivityViewModel.getProjectModel().getChosenPosition()
                        ).setDeadlineColumnIndex(boardViewModel.getDeadlineColumnIndex());

                        thisDialog.dismiss();
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtils.showToastError(ProjectActivity.this, "Something went wrong", Toast.LENGTH_SHORT);
                    }
                });
            }

            @Override
            public void onCancel(Dialog thisDialog) {
                thisDialog.dismiss();
            }
        });
    }

    private void onCheckboxItemClicked(BoardCheckboxItemModel itemModel, int columnPos, int rowPos) {
        Dialog loadingDialog = DialogUtils.GetLoadingDialog(ProjectActivity.this);
        loadingDialog.show();
        itemModel.setChecked(!itemModel.getChecked());
        boardViewModel.updateACell(itemModel).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    boardAdapter.changeCellItem(columnPos, rowPos, itemModel);
                } else {
                    ToastUtils.showToastError(ProjectActivity.this, "Unable to update the cell", Toast.LENGTH_LONG);
                }

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                ToastUtils.showToastError(ProjectActivity.this, "Unable to update the cell", Toast.LENGTH_LONG);
                loadingDialog.dismiss();
            }
        });
    }

    /**
     * The reason to use onStart() is the onStop() will eventually navigate to this
     * so it will update the project (fetch data)
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (!isNewProjectCreateRequest) updateProject();
    }

    /**
     * TODO: better way to handle this "createNew"
     * @whatToDo is the thing that specify how the activity should handle
     * the case
     * One is create new board, it needs to send and get data from server ("createNew")
     * else is to fetch the board which is created before
     */

    private void createNewProjectIfRequest() {
        Intent intent = getIntent();
        String whatToDo = intent.getStringExtra("whatToDo");
        Dialog loadingDialog = DialogUtils.GetLoadingDialog(this);
        if (whatToDo != null && whatToDo.equals("createNew")) {
            String templateName = intent.getStringExtra("templateName");
            if (templateName == null) {
                ToastUtils.showToastError(ProjectActivity.this, "Something went wrong, please try again", Toast.LENGTH_LONG);
                finish();
                return;
            }
            isNewProjectCreateRequest = true;
            loadingDialog.show();
            projectActivityViewModel.saveNewProjectToRemote(new ProjectActivityViewModel.ApiCallHandlers() {
                @Override
                public void onSuccess() {
                    loadingDialog.dismiss();
                    isNewProjectCreateRequest = false;
                    saveRecentAccessProject(projectActivityViewModel.getProjectId());
                    SharedPreferencesManager.saveData(SharedPreferencesManager.KEYS.CURRENT_PROJECT_ID, projectActivityViewModel.getProjectId());
                }

                @Override
                public void onFailure(String message) {
                    ToastUtils.showToastError(ProjectActivity.this, "Failed to create new project, please try again", Toast.LENGTH_LONG);
                    loadingDialog.dismiss();
                    finish();
                }
            }, templateName);
        }
    }

    private void updateProject() {
        Dialog loadingDialog = DialogUtils.GetLoadingDialog(ProjectActivity.this);
        loadingDialog.show();
        String projectId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.CURRENT_PROJECT_ID);
        projectActivityViewModel.getProjectById(projectId, new ProjectActivityViewModel.ApiCallHandlers() {
            @Override
            public void onSuccess() {
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(String message) {
                loadingDialog.dismiss();
                ToastUtils.showToastError(ProjectActivity.this, message, Toast.LENGTH_LONG);
                finish();
            }
        });
    }

    private void showTables() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardEditBoardsViewBinding binding = BoardEditBoardsViewBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        EditBoardsAdapter editBoardsAdapter;
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        if(projectActivityViewModel.getProjectModel().getCreatorId().equals(userId))
        {
            editBoardsAdapter = new EditBoardsAdapter(this.projectActivityViewModel, this, false);
            binding.layoutAddBoard.setVisibility(View.VISIBLE);
        }
        else if (projectActivityViewModel.getProjectModel().getAdminIds().contains(userId))
        {
            editBoardsAdapter = new EditBoardsAdapter(this.projectActivityViewModel, this, false);
            binding.layoutAddBoard.setVisibility(View.VISIBLE);
        }
        else
        {
            editBoardsAdapter = new EditBoardsAdapter(this.projectActivityViewModel, this, true);
            binding.layoutAddBoard.setVisibility(View.GONE);
        }

        editBoardsAdapter.setHandlers(new EditBoardsAdapter.ClickHandlers() {
            @Override
            public void onRemoveClick(int position) {
                editBoardsAdapter.notifyItemRemoved(position);
                editBoardsAdapter.notifyItemRangeChanged(position, projectActivityViewModel.getProjectModel().getBoards().size());
            }

            @Override
            public void onRenameClick(int position, String newTitle) {
                editBoardsAdapter.notifyItemChanged(position);
                if (position == projectActivityViewModel.getProjectModel().getChosenPosition()) {
                    boardViewModel.setBoardTitle(newTitle);
                    activityBinding.btnShowTables.setText(newTitle);
                }
            }

            @Override
            public void onItemClick(int position) {
                listSelectedCollection.clear();
                activityBinding.emptyFilterResult.setVisibility(View.GONE);
                projectActivityViewModel.getProjectModel().setChosenPosition(position);
                BoardContentModel newContent = projectActivityViewModel.getProjectModel().getBoards().get(position);
                boardViewModel.setBoardContent(newContent, projectActivityViewModel.getProjectModel().get_id(), boardAdapter);
                activityBinding.btnShowTables.setText(newContent.getBoardTitle());
                dialog.dismiss();
            }
        });

        binding.rvBoards.setLayoutManager(new LinearLayoutManager(this));
        binding.rvBoards.setAdapter(editBoardsAdapter);

        binding.btnClose.setOnClickListener(view -> {
            boardViewModel.setBoardContent(
                    projectActivityViewModel.getProjectModel().getBoards().get(
                            projectActivityViewModel.getProjectModel().getChosenPosition()
                    ),
                    projectActivityViewModel.getProjectId(),
                    boardAdapter)
            ;
            dialog.dismiss();
        });

        binding.btnNewBoard.setOnClickListener(view -> {
            Dialog loadingDialog = DialogUtils.GetLoadingDialog(ProjectActivity.this);
            loadingDialog.show();
            projectActivityViewModel.addNewBoardToProject(new ProjectActivityViewModel.ApiCallHandlers() {
                @Override
                public void onSuccess() {
                    editBoardsAdapter.notifyItemInserted(projectActivityViewModel.getProjectModel().getBoards().size() - 1);
                    loadingDialog.dismiss();
                }

                @Override
                public void onFailure(String message) {
                    ToastUtils.showToastError(ProjectActivity.this, "Unable to add new board, please try again", Toast.LENGTH_LONG);
                    loadingDialog.dismiss();
                }
            });
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void showPopupFilter(){
        final Dialog dialog = new Dialog(ProjectActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        PopupFilterBoardBinding popupFilterBinding = PopupFilterBoardBinding.inflate(getLayoutInflater());
        dialog.setContentView(popupFilterBinding.getRoot());

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        popupFilterBinding.btnClosePopup.setOnClickListener(view -> dialog.dismiss());

        //setup popup filter with items to filter
        popupFilterBinding.rvBoardFilter.setLayoutManager(new LinearLayoutManager(ProjectActivity.this));
        // consists of board names
        List<FilterModel> listFilterBoard = new ArrayList<>();
        List<List<String>> listFilterCollection = new ArrayList<>();
        List<List<String>> tempListSelectedCollection = new ArrayList<>();
        for (List<String> selectedCollection : listSelectedCollection) {
            List<String> newList = new ArrayList<>(selectedCollection);
            tempListSelectedCollection.add(newList);
        }

        List<BoardColumnHeaderModel> listColumn = boardViewModel.getmColumnHeaderModelList();
        listColumn.forEach(column -> {
            if(column.getColumnType() == BoardColumnHeaderModel.ColumnType.User)
            {
                //get index of column that i am getting it's title
                int indexColumn = listColumn.indexOf(column);
                //add title of column
                listFilterBoard.add(new FilterModel("By " + column.getTitle(), FilterModel.TypeFilter.AVATAR));
                List<String> filterCollection = new ArrayList<>();
                List<String> selectedCollection = new ArrayList<>();
                //get content of all cells in that column
                List<List<BoardBaseItemModel>> board = boardViewModel.getmCellModelList();
                board.forEach(row -> {
                    BoardUserItemModel userItem = (BoardUserItemModel)row.get(indexColumn);
                    if(!userItem.getUsers().isEmpty())
                    {
                        userItem.getUsers().forEach(user -> {
                            if(!filterCollection.contains(user.getId()))
                                filterCollection.add(user.getId());
                        });
                    }
                });

                listFilterCollection.add(filterCollection);

                if(indexColumn < tempListSelectedCollection.size())
                    selectedCollection.addAll(tempListSelectedCollection.get(indexColumn));
                else {
                    tempListSelectedCollection.add(selectedCollection);
                }
            }
            else if (column.getColumnType() == BoardColumnHeaderModel.ColumnType.NewColumn)
            {
                //do no thing to this column
            }
            else
            {
                //get index of column that i am getting its title
                int indexColumn = listColumn.indexOf(column);
                //add title of column
                listFilterBoard.add(new FilterModel("By " + column.getTitle(), FilterModel.TypeFilter.TEXT));
                List<String> filterCollection = new ArrayList<>();
                List<String> selectedCollection = new ArrayList<>();
                //get content of all cells in that column
                List<List<BoardBaseItemModel>> board = boardViewModel.getmCellModelList();
                board.forEach(row -> {
                    String content = row.get(indexColumn).getContent();
                    if(!content.isEmpty())
                    {
                        if(!filterCollection.contains(content))
                            filterCollection.add(row.get(indexColumn).getContent());
                    }
                });
                listFilterCollection.add(filterCollection);

                if(indexColumn < tempListSelectedCollection.size())
                    selectedCollection.addAll(tempListSelectedCollection.get(indexColumn));
                else {
                    tempListSelectedCollection.add(selectedCollection);
                }
            }
        });

        BoardFilterAdapter filterBoardAdapter = new BoardFilterAdapter(listFilterBoard, listFilterCollection, tempListSelectedCollection);
        popupFilterBinding.rvBoardFilter.setAdapter(filterBoardAdapter);

        popupFilterBinding.btnDone.setOnClickListener(view -> {
            listSelectedCollection.clear();
            for (List<String> selectedCollection : tempListSelectedCollection) {
                List<String> newList = new ArrayList<>(selectedCollection);
                listSelectedCollection.add(newList);
            }
            boardViewModel.filterRow(listSelectedCollection, boardAdapter, activityBinding);
            dialog.dismiss();
        });

        popupFilterBinding.btnClear.setOnClickListener(view -> {
            tempListSelectedCollection.clear();
            filterBoardAdapter.notifyDataSetChanged();
        });

    }

    private void showNewRowPopup() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardAddNewRowPopupBinding binding = BoardAddNewRowPopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        binding.btnClosePopup.setOnClickListener(view -> dialog.dismiss());
        binding.btnAdd.setOnClickListener(view -> {
            String newRowTitle = binding.etContent.getText().toString();
            if (newRowTitle.isEmpty()) { dialog.dismiss(); return; }
            boardViewModel.createNewRow(newRowTitle, new BoardViewModel.ApiCallHandler() {
                @Override
                public void onSuccess() {
                    updateProject();
                }

                @Override
                public void onFailure(String message) {

                }
            });
            dialog.dismiss();
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }

    private void showOwnerPopup(BoardUserItemModel userItemModel, String columnTitle, int columnPos, int rowPos) {
        final Dialog dialog = new Dialog(ProjectActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardOwnerItemPopupBinding binding = BoardOwnerItemPopupBinding.inflate(getLayoutInflater());
        binding.popupTitle.setText(columnTitle);
        dialog.setContentView(binding.getRoot());

        List<UserModel> listMember = new ArrayList<>();
        List<UserModel> listOwner = new ArrayList<>(userItemModel.getUsers());
        binding.rvMembers.setLayoutManager(new LinearLayoutManager(ProjectActivity.this));
        binding.rvOwers.setLayoutManager(new LinearLayoutManager(ProjectActivity.this, LinearLayoutManager.HORIZONTAL, false));
        BoardItemOwnerAdapter boardItemOwnerAdapter;
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        if(projectActivityViewModel.getProjectModel().getCreatorId().equals(userId))
            boardItemOwnerAdapter = new BoardItemOwnerAdapter(listOwner, false);
        else if (projectActivityViewModel.getProjectModel().getAdminIds().contains(userId))
            boardItemOwnerAdapter = new BoardItemOwnerAdapter(listOwner, false);
        else
            boardItemOwnerAdapter = new BoardItemOwnerAdapter(listOwner, true);

        BoardItemMemberAdapter boardItemMemberAdapter = new BoardItemMemberAdapter();
        binding.rvMembers.setAdapter(boardItemMemberAdapter);
        binding.rvOwers.setAdapter(boardItemOwnerAdapter);

        String projectId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.CURRENT_PROJECT_ID);
        projectActivityViewModel.getMember(projectId, new ProjectActivityViewModel.ApiCallMemberHandlers() {
            @Override
            public void onSuccess(List<UserModel> listMemberData) {
                listMember.addAll(listMemberData);
                List<Boolean> statuses = new ArrayList<>();
                for (int i = 0; i < listMember.size(); i++) {
                    boolean isSet = false;
                    for (int j = 0; j < userItemModel.getUsers().size(); j++) {
                        if (userItemModel.getUsers().get(j).getId().equals(listMember.get(i).getId()))
                            isSet = true;
                    }
                    statuses.add(isSet);
                }
                boardItemMemberAdapter.setData(listMember, statuses);
                binding.rvMembers.setAdapter(boardItemMemberAdapter);

                String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
                if(projectActivityViewModel.getProjectModel().getCreatorId().equals(userId))
                    showPopupOwnerFor(Role.CREATOR, binding);
                else if (projectActivityViewModel.getProjectModel().getAdminIds().contains(userId))
                    showPopupOwnerFor(Role.ADMIN, binding);
                else
                    showPopupOwnerFor(Role.MEMBER, binding);
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(ProjectActivity.this, message, Toast.LENGTH_SHORT);
            }
        });

        boardItemMemberAdapter.setOnClickListener((position, status) -> {
            if (status) {
                listOwner.add(listMember.get(position));
                boardItemOwnerAdapter.notifyItemInserted(listOwner.size() - 1);
                binding.layoutRvOwers.setVisibility(View.VISIBLE);
            } else {
                listOwner.removeIf(userModel -> userModel.getId().equals(listMember.get(position).getId()));
                boardItemOwnerAdapter.notifyDataSetChanged();
                if(listOwner.size() > 0) binding.layoutRvOwers.setVisibility(View.VISIBLE);
                else binding.layoutRvOwers.setVisibility(View.GONE);
            }
        });

        boardItemOwnerAdapter.setOnClickListener(position -> {
            UserModel deletedModel = listOwner.remove(position);
            boardItemOwnerAdapter.notifyItemRemoved(position);
            boardItemOwnerAdapter.notifyItemRangeChanged(position, listOwner.size());

            for (int i = 0; i < listMember.size(); i++) {
                if (listMember.get(i).getId().equals(deletedModel.getId())) {
                    boardItemMemberAdapter.setStatusAt(i, false);
                    break;
                }
            }

            if(listOwner.size() > 0) binding.layoutRvOwers.setVisibility(View.VISIBLE);
            else binding.layoutRvOwers.setVisibility(View.GONE);
        });

        binding.btnSave.setOnClickListener(view -> {
            userItemModel.setUsers(listOwner);
            boardViewModel.updateACell(userItemModel).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        boardAdapter.changeCellItem(columnPos, rowPos, userItemModel);
                        ToastUtils.showToastSuccess(ProjectActivity.this, "Updated", Toast.LENGTH_SHORT);
                    } else ToastUtils.showToastError(ProjectActivity.this, response.message(), Toast.LENGTH_SHORT);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    ToastUtils.showToastError(ProjectActivity.this, t.getMessage(), Toast.LENGTH_SHORT);
                }
            });
            dialog.dismiss();
        });
        binding.btnClosePopup.setOnClickListener(view -> dialog.dismiss());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void showTaskStatusPopup(BoardStatusItemModel statusItemModel, String columnTitle, int columnPos, int rowPos)
    {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardStatusItemPopupBinding binding = BoardStatusItemPopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        binding.tvStatusPopupTitle.setText(columnTitle);
        StatusContentsAdapter statusContentsAdapter = new StatusContentsAdapter(statusItemModel);
        statusContentsAdapter.setHandlers((itemModel, newContent) -> {
            itemModel.setContent(newContent);
            Dialog loadingDialog = DialogUtils.GetLoadingDialog(ProjectActivity.this);
            loadingDialog.show();
            boardViewModel.updateACell(itemModel).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        boardAdapter.changeCellItem(columnPos, rowPos, itemModel);
                    } else {
                        ToastUtils.showToastError(ProjectActivity.this, "Unable to save the cell", Toast.LENGTH_LONG);
                    }

                    loadingDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    ToastUtils.showToastError(ProjectActivity.this, "Unable to save the cell", Toast.LENGTH_LONG);
                    loadingDialog.dismiss();
                    dialog.dismiss();
                }
            });
        });
        binding.rvStatusContents.setLayoutManager(new LinearLayoutManager(this));
        binding.rvStatusContents.setAdapter(statusContentsAdapter);

        binding.btnClose.setOnClickListener(view -> dialog.dismiss());
        binding.btnEditLabels.setOnClickListener(view -> {
            showStatusContentsEdit(statusItemModel, statusContentsAdapter, columnPos, rowPos);
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void showStatusContentsEdit(BoardStatusItemModel statusItemModel, StatusContentsAdapter statusContentsAdapter, int columnPos, int rowPos) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardStatusEditViewBinding binding = BoardStatusEditViewBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        BoardStatusItemModel clonedItemModel = new BoardStatusItemModel(statusItemModel);

        StatusEditItemAdapter statusEditItemAdapter = new StatusEditItemAdapter(clonedItemModel);
        statusEditItemAdapter.setHandlers(new StatusEditItemAdapter.ClickHandlers() {
            @Override
            public void onChooseColorClick(int position, BoardStatusItemModel itemModel) {
                new ColorPickerDialog.Builder(ProjectActivity.this)
                        .setTitle("Choose color")
                        .setPositiveButton("SELECT", (ColorEnvelopeListener) (envelope, fromUser) -> {
                            itemModel.setColorAt(position, '#' + envelope.getHexCode());
                            statusEditItemAdapter.notifyItemChanged(position);
                        })
                        .setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                        .attachAlphaSlideBar(true)
                        .attachBrightnessSlideBar(true)
                        .setBottomSpace(12)
                        .show();
            }

            @Override
            public void onDeleteClick(int position, BoardStatusItemModel itemModel) {
                if (position >= itemModel.getContents().size()) return;
                if (Objects.equals(itemModel.getContent(), itemModel.getContents().get(position))) itemModel.setContent("");
                itemModel.removeContentAt(position);
                statusEditItemAdapter.notifyItemRemoved(position);
                statusEditItemAdapter.notifyItemRangeChanged(position, itemModel.getContents().size());
            }
        });

        binding.rvStatusItems.setLayoutManager(new LinearLayoutManager(ProjectActivity.this, LinearLayoutManager.VERTICAL, false));
        binding.rvStatusItems.setAdapter(statusEditItemAdapter);
        binding.btnAdd.setOnClickListener(view -> {
            showAddNewStatusDialog(clonedItemModel, statusEditItemAdapter);
        });

        binding.btnClose.setOnClickListener(view -> dialog.dismiss());
        binding.btnSave.setOnClickListener(view -> {
            statusItemModel.copyDataFromAnotherInstance(clonedItemModel);

            Dialog loadingDialog = DialogUtils.GetLoadingDialog(ProjectActivity.this);
            loadingDialog.show();
            boardViewModel.updateACell(statusItemModel).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        boardAdapter.changeCellItem(columnPos, rowPos, statusItemModel);
                        statusContentsAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtils.showToastError(ProjectActivity.this, "Unable to save", Toast.LENGTH_LONG);
                    }

                    loadingDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    ToastUtils.showToastError(ProjectActivity.this, "Unable to save", Toast.LENGTH_LONG);
                    loadingDialog.dismiss();
                    dialog.dismiss();
                }
            });
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void showAddNewStatusDialog(BoardStatusItemModel itemModel, StatusEditItemAdapter statusEditItemAdapter) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardStatusEditNewItemPopupBinding binding = BoardStatusEditNewItemPopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        binding.btnClosePopup.setOnClickListener((view) -> dialog.dismiss());
        binding.btnAdd.setOnClickListener(view -> {
            String newContent = binding.etTextItem.getText().toString();
            for (int i = 0; i < itemModel.getContents().size(); i++) {
                if (itemModel.getContents().get(i).equals(newContent)) {
                    ToastUtils.showToastError(ProjectActivity.this, "Content already existed", Toast.LENGTH_LONG);
                    return;
                }
            }
            itemModel.addNewContent(newContent);
            statusEditItemAdapter.notifyItemInserted(itemModel.getContents().size() - 1);
            dialog.dismiss();
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }

    private void showTextItemPopup(BoardTextItemModel itemModel, String title, int columnPos, int rowPos) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardTextItemPopupBinding binding = BoardTextItemPopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        binding.textItemTitle.setText(title);
        binding.etTextItem.setText(itemModel.getContent());
        binding.btnClosePopup.setOnClickListener((view) -> dialog.dismiss());

        binding.btnSaveTextItem.setOnClickListener(view -> {
            String newContent = String.valueOf(binding.etTextItem.getText());
            itemModel.setContent(newContent);

            Dialog loadingDialog = DialogUtils.GetLoadingDialog(ProjectActivity.this);
            loadingDialog.show();
            boardViewModel.updateACell(itemModel).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        boardAdapter.changeCellItem(columnPos, rowPos, itemModel);
                    } else {
                        ToastUtils.showToastError(ProjectActivity.this, "Unable to update the cell", Toast.LENGTH_LONG);
                    }

                    loadingDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    ToastUtils.showToastError(ProjectActivity.this, "Unable to update the cell", Toast.LENGTH_LONG);
                    loadingDialog.dismiss();
                    dialog.dismiss();
                }
            });
        });

        binding.btnClearTextItem.setOnClickListener((view) -> binding.etTextItem.setText(""));

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void showNumberItemPopup(BoardNumberItemModel itemModel, String title, int columnPos, int rowPos) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardNumberItemPopupBinding binding = BoardNumberItemPopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        binding.textNumberTitle.setText(title);
        binding.etNumberItem.setText(itemModel.getContent());
        binding.btnClosePopup.setOnClickListener((view) -> dialog.dismiss());
        binding.btnSaveNumberItem.setOnClickListener(view -> {
            String newContent = String.valueOf(binding.etNumberItem.getText());
            itemModel.setContent(newContent);

            Dialog loadingDialog = DialogUtils.GetLoadingDialog(ProjectActivity.this);
            loadingDialog.show();
            boardViewModel.updateACell(itemModel).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        boardAdapter.changeCellItem(columnPos, rowPos, itemModel);
                    } else {
                        ToastUtils.showToastError(ProjectActivity.this, "Unable to update the cell", Toast.LENGTH_LONG);
                    }

                    loadingDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    ToastUtils.showToastError(ProjectActivity.this, "Unable to update the cell", Toast.LENGTH_LONG);
                    loadingDialog.dismiss();
                    dialog.dismiss();
                }
            });

            boardAdapter.changeCellItem(columnPos, rowPos, itemModel);
            dialog.dismiss();
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void showTimelineItemPopup(BoardTimelineItemModel itemModel, String title, int columnPos, int rowPos) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardTimelineItemPopupBinding binding = BoardTimelineItemPopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        final AtomicInteger dialogStartYear = new AtomicInteger(-1);
        final AtomicInteger dialogStartMonth = new AtomicInteger(-1);
        final AtomicInteger dialogStartDay = new AtomicInteger(-1);
        final AtomicInteger dialogEndYear = new AtomicInteger(-1);
        final AtomicInteger dialogEndMonth = new AtomicInteger(-1);
        final AtomicInteger dialogEndDay = new AtomicInteger(-1);

        if (!itemModel.getContent().isEmpty()) {
            dialogStartDay.set(itemModel.getStartDay());
            dialogStartMonth.set(itemModel.getStartMonth());
            dialogStartYear.set(itemModel.getStartYear());
            dialogEndDay.set(itemModel.getEndDay());
            dialogEndMonth.set(itemModel.getEndMonth());
            dialogEndYear.set(itemModel.getEndYear());
            binding.tvTimelineValue.setText(itemModel.getContent());
            binding.tvAddTimelineTitle.setText("Clear");
            binding.tvAddTimelineTitle.setOnClickListener((view) -> {
                dialogStartDay.set(-1);
                dialogStartMonth.set(-1);
                dialogStartYear.set(-1);
                dialogEndDay.set(-1);
                dialogEndMonth.set(-1);
                dialogEndYear.set(-1);
                binding.tvTimelineValue.setText("");
                binding.tvAddTimelineTitle.setText("Add time");
                binding.tvAddTimelineTitle.setOnClickListener(null);
            });
        }

        binding.tvTimelineItemTitle.setText(title);
        binding.btnClosePopup.setOnClickListener((view) -> dialog.dismiss());

        binding.btnSaveTimelineItem.setOnClickListener(view -> {
            Dialog loadingDialog = DialogUtils.GetLoadingDialog(ProjectActivity.this);
            loadingDialog.show();

            // TODO: The function expects no problems or exceptions, should not update the item if the call failed
            itemModel.setStartYear(dialogStartYear.get());
            itemModel.setStartMonth(dialogStartMonth.get());
            itemModel.setStartDay(dialogStartDay.get());
            itemModel.setEndYear(dialogEndYear.get());
            itemModel.setEndMonth(dialogEndMonth.get());
            itemModel.setEndDay(dialogEndDay.get());

            boardViewModel.updateACell(itemModel).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        boardAdapter.changeCellItem(columnPos, rowPos, itemModel);
                    } else {
                        ToastUtils.showToastError(ProjectActivity.this, "Unable to save the cell", Toast.LENGTH_LONG);
                    }
                    loadingDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    ToastUtils.showToastError(ProjectActivity.this, "Unable to save the cell", Toast.LENGTH_LONG);
                    loadingDialog.dismiss();
                    dialog.dismiss();
                }
            });
        });

        binding.addTimeContainer.setOnClickListener((view) -> {
            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setSelection(Pair.create(
                        CustomUtils.getTimeInMillis(dialogStartDay.get(), dialogStartMonth.get(), dialogStartYear.get()),
                        CustomUtils.getTimeInMillis(dialogEndDay.get(), dialogEndMonth.get(), dialogEndYear.get())
                ))
                .build();

            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String startDate = Instant
                            .ofEpochMilli(selection.first)
                            .atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    String[] startData = startDate.split("-");
                    dialogStartDay.set(Integer.parseInt(startData[0]));
                    dialogStartMonth.set(Integer.parseInt(startData[1]));
                    dialogStartYear.set(Integer.parseInt(startData[2]));

                    String endDate = Instant
                            .ofEpochMilli(selection.second)
                            .atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    String[] endData = endDate.split("-");
                    dialogEndDay.set(Integer.parseInt(endData[0]));
                    dialogEndMonth.set(Integer.parseInt(endData[1]));
                    dialogEndYear.set(Integer.parseInt(endData[2]));
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(selection.first);
                    dialogStartDay.set(calendar.get(Calendar.DAY_OF_MONTH));
                    dialogStartMonth.set(calendar.get(Calendar.MONTH));
                    dialogStartYear.set(calendar.get(Calendar.YEAR));

                    calendar.setTimeInMillis(selection.second);
                    dialogEndDay.set(calendar.get(Calendar.DAY_OF_MONTH));
                    dialogEndMonth.set(calendar.get(Calendar.MONTH));
                    dialogEndYear.set(calendar.get(Calendar.YEAR));
                }

                String finalContent = "";
                if (dialogStartDay.get() != -1 && dialogStartMonth.get() != -1 && dialogStartYear.get() != -1) {
                    if (dialogStartDay.get() == dialogEndDay.get() && dialogStartMonth.get() == dialogStartMonth.get() && dialogStartYear.get() == dialogEndYear.get()) {

                    } else if (dialogStartMonth.get() == dialogStartMonth.get() && dialogStartYear.get() == dialogEndYear.get())
                        finalContent += String.format(Locale.US, "%d", dialogStartDay.get());
                    else if (dialogStartYear.get() == dialogEndYear.get()) {
                        finalContent += String.format(Locale.US, "%s %d", CustomUtils.convertIntToMonth(dialogStartMonth.get()), dialogStartDay.get());
                    } else finalContent += String.format(Locale.US, "%s %d, %d", CustomUtils.convertIntToMonth(dialogStartMonth.get()), dialogStartDay.get(), dialogStartYear.get());

                    if (finalContent.isEmpty())
                        finalContent += String.format(Locale.US, "%s %d, %d", CustomUtils.convertIntToMonth(dialogStartMonth.get()), dialogEndDay.get(), dialogEndYear.get());
                    else finalContent += String.format(Locale.US, " - %s %d, %d", CustomUtils.convertIntToMonth(dialogStartMonth.get()), dialogEndDay.get(), dialogEndYear.get());
                }

                binding.tvTimelineValue.setText(finalContent);
                binding.tvAddTimelineTitle.setText("Clear");
                binding.tvAddTimelineTitle.setOnClickListener((lolView) -> {
                    dialogStartDay.set(-1);
                    dialogStartMonth.set(-1);
                    dialogStartYear.set(-1);
                    dialogEndDay.set(-1);
                    dialogEndMonth.set(-1);
                    dialogEndYear.set(-1);
                    binding.tvTimelineValue.setText("");
                    binding.tvAddTimelineTitle.setText("Add time");
                    binding.tvAddTimelineTitle.setOnClickListener(null);
                });
            });

            materialDatePicker.show(getSupportFragmentManager(), "I DONT KNOW WHAT THIS IS");
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void showDateItemPopup(@NonNull BoardDateItemModel itemModel, String title, int columnPos, int rowPos) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardDateItemPopupBinding binding = BoardDateItemPopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        final AtomicInteger dialogYear = new AtomicInteger(itemModel.getYear());
        final AtomicInteger dialogMonth = new AtomicInteger(itemModel.getMonth());
        final AtomicInteger dialogDay = new AtomicInteger(itemModel.getDay());
        final AtomicInteger dialogHour = new AtomicInteger(itemModel.getHour());
        final AtomicInteger dialogMinute = new AtomicInteger(itemModel.getMinute());

        if (!itemModel.getDate().isEmpty()) {
            binding.tvDateValue.setText(itemModel.getDate());
            binding.tvAddDateTitle.setText("Clear date");
            binding.tvAddDateTitle.setOnClickListener((view) -> {
                dialogYear.set(-1);
                dialogMonth.set(-1);
                dialogDay.set(-1);
                binding.tvDateValue.setText("");
                binding.tvAddDateTitle.setText("Add date");
                binding.tvAddDateTitle.setOnClickListener(null);
            });
        }
        if (!itemModel.getTime().isEmpty()) {
            binding.tvTimeValue.setText(itemModel.getTime());
            binding.tvAddTimeTitle.setText("Clear time");
            binding.tvAddTimeTitle.setOnClickListener((view) -> {
                dialogHour.set(-1);
                dialogMinute.set(-1);
                binding.tvTimeValue.setText("");
                binding.tvAddTimeTitle.setText("Add time");
                binding.tvAddTimeTitle.setOnClickListener(null);
            });
        }

        binding.tvDateItemTitle.setText(title);
        binding.btnClosePopup.setOnClickListener((view) -> dialog.dismiss());
        binding.btnSaveDateItem.setOnClickListener(view -> {
            itemModel.setYear(dialogYear.get());
            itemModel.setMonth(dialogMonth.get());
            itemModel.setDay(dialogDay.get());
            itemModel.setHour(dialogHour.get());
            itemModel.setMinute(dialogMinute.get());

            Dialog loadingDialog = DialogUtils.GetLoadingDialog(ProjectActivity.this);
            loadingDialog.show();
            Call<Void> cellUpdateCall = boardViewModel.updateACell(itemModel);
            cellUpdateCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        boardAdapter.changeCellItem(columnPos, rowPos, itemModel);
                    } else {
                        ToastUtils.showToastError(ProjectActivity.this, "Unable to update the cell", Toast.LENGTH_LONG);
                    }

                    loadingDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    ToastUtils.showToastError(ProjectActivity.this, "Unable to update the cell", Toast.LENGTH_LONG);
                    loadingDialog.dismiss();
                    dialog.dismiss();
                }
            });

        });

        binding.dateItemDateContainer.setOnClickListener((view) -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ProjectActivity.this,
                    null,
                    dialogYear.get() == -1 ? calendar.get(Calendar.YEAR) : dialogYear.get(),
                    dialogMonth.get() == -1 ? calendar.get(Calendar.MONTH) : dialogMonth.get(),
                    dialogDay.get() == - 1 ? calendar.get(Calendar.DAY_OF_MONTH) : dialogDay.get()
            );
            datePickerDialog.setOnDateSetListener((datePicker, newYear, newMonth, newDay) -> {
                dialogYear.set(newYear);
                dialogMonth.set(newMonth);
                dialogDay.set(newDay);
                binding.tvDateValue.setText(String.format(Locale.US,"%s %d, %d", CustomUtils.convertIntToMonth(dialogMonth.get()), dialogDay.get(), dialogYear.get()));
                binding.tvAddDateTitle.setText("Clear date");
                binding.tvAddDateTitle.setOnClickListener((titleView) -> {
                    dialogYear.set(-1);
                    dialogMonth.set(-1);
                    dialogDay.set(-1);
                    binding.tvDateValue.setText("");
                    binding.tvAddDateTitle.setText("Add date");
                    binding.tvAddDateTitle.setOnClickListener(null);
                });
            });
            datePickerDialog.show();
        });

        binding.dateItemTimeContainer.setOnClickListener((view) -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(ProjectActivity.this,
                    (timePicker, newHour, newMinute) -> {
                        dialogHour.set(newHour);
                        dialogMinute.set(newMinute);
                        binding.tvTimeValue.setText(String.format(Locale.US, "%02d:%02d", dialogHour.get(), dialogMinute.get()));
                        binding.tvAddTimeTitle.setText("Clear time");
                        binding.tvAddTimeTitle.setOnClickListener((titleView) -> {
                            dialogHour.set(-1);
                            dialogMinute.set(-1);
                            binding.tvTimeValue.setText("");
                            binding.tvAddTimeTitle.setText("Add time");
                            binding.tvAddTimeTitle.setOnClickListener(null);
                        });
                    },
                    dialogHour.get() == -1 ? calendar.get(Calendar.HOUR_OF_DAY) : dialogHour.get(),
                    dialogMinute.get() == - 1 ? calendar.get(Calendar.MINUTE) : dialogMinute.get(),
                    true
            ).show();
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void showAddBoardItemPopup() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardAddItemPopupBinding binding = BoardAddItemPopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        BoardViewModel.ApiCallHandler handler = new BoardViewModel.ApiCallHandler() {
            @Override
            public void onSuccess() {
                updateProject();
            }

            @Override
            public void onFailure(String message) {}
        };

        binding.btnClosePopup.setOnClickListener((view) -> dialog.dismiss());
        binding.btnAddTextItem.setOnClickListener((view) -> {
            boardViewModel.createNewColumn(BoardColumnHeaderModel.ColumnType.Text, handler);
        });

        binding.btnAddUserItem.setOnClickListener((view) -> {
            boardViewModel.createNewColumn(BoardColumnHeaderModel.ColumnType.User, handler);
        });

        binding.btnAddStatusItem.setOnClickListener((view) -> {
            boardViewModel.createNewColumn(BoardColumnHeaderModel.ColumnType.Status, handler);
        });

        binding.btnAddNumberItem.setOnClickListener((view -> {
            boardViewModel.createNewColumn(BoardColumnHeaderModel.ColumnType.Number, handler);
        }));

        binding.btnAddUpdateItem.setOnClickListener((view -> {
            boardViewModel.createNewColumn(BoardColumnHeaderModel.ColumnType.Update, handler);
        }));

        binding.btnAddCheckboxItem.setOnClickListener((view -> {
            boardViewModel.createNewColumn(BoardColumnHeaderModel.ColumnType.Checkbox, handler);
        }));

        binding.btnAddDateItem.setOnClickListener((view) -> {
            boardViewModel.createNewColumn(BoardColumnHeaderModel.ColumnType.Date, handler);
        });

        binding.btnAddTimelineItem.setOnClickListener((view) -> {
            boardViewModel.createNewColumn(BoardColumnHeaderModel.ColumnType.TimeLine, handler);
        });

        binding.btnAddMapItem.setOnClickListener((view) -> {
            boardViewModel.createNewColumn(BoardColumnHeaderModel.ColumnType.Map, handler);
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void saveRecentAccessProject(String projectId)
    {
        if(projectId == null) return;
        userViewModel.saveRecentProjectId(projectId, new UserViewModel.DefaultCallback() {
            @Override
            public void onSuccess() {
                //just save recent access and do no thing when success
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(ProjectActivity.this, message, Toast.LENGTH_SHORT);
            }
        });
    }
    private boolean isAnOwnerOfRow(String id, int rowPos)
    {
        if(boardViewModel.getmCellModelList().get(rowPos) == null) return false;
        List<BoardBaseItemModel> cells = boardViewModel.getmCellModelList().get(rowPos);

        for (BoardBaseItemModel cell : cells) {
            if (cell.getCellType().equals("CellUser")) {
                BoardUserItemModel userItemModel = (BoardUserItemModel) cell;
                if (userItemModel != null) {
                    List<UserModel> users = userItemModel.getUsers();
                    for (UserModel user : users) {
                        if (user.getId().equals(id)) {
                            return true;
                        }
                    }
                }
            }
        }
        ToastUtils.showToastError(ProjectActivity.this, "You don't have permission to adjust this row", Toast.LENGTH_SHORT);
        return false;
    }
}