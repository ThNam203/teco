package com.worthybitbuilders.squadsense.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.activities.MapActivity;
import com.worthybitbuilders.squadsense.activities.ProjectActivity;
import com.worthybitbuilders.squadsense.adapters.BoardItemDetailColumnAdapter;
import com.worthybitbuilders.squadsense.adapters.BoardItemMemberAdapter;
import com.worthybitbuilders.squadsense.adapters.BoardItemOwnerAdapter;
import com.worthybitbuilders.squadsense.adapters.StatusContentsAdapter;
import com.worthybitbuilders.squadsense.adapters.StatusEditItemAdapter;
import com.worthybitbuilders.squadsense.databinding.BoardDateItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardNumberItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardOwnerItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardStatusEditNewItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardStatusEditViewBinding;
import com.worthybitbuilders.squadsense.databinding.BoardStatusItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardTextItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardTimelineItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.FragmentBoardDetailColumnBinding;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardCheckboxItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardDateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardMapItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardNumberItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardStatusItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTextItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTimelineItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUpdateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;
import com.worthybitbuilders.squadsense.utils.CustomUtils;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.BoardDetailItemViewModel;
import com.worthybitbuilders.squadsense.viewmodels.ProjectActivityViewModel;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardDetailColumnFragment extends Fragment {
    BoardDetailItemViewModel viewModel;
    ProjectActivityViewModel projectActivityViewModel;
    private FragmentBoardDetailColumnBinding binding;
    private BoardItemDetailColumnAdapter adapter;

    private String creatorId = "";
    private List<String> listAdminId = new ArrayList<>();

    public static BoardDetailColumnFragment newInstance() {
        BoardDetailColumnFragment fragment = new BoardDetailColumnFragment();
        return fragment;
    }
    public BoardDetailColumnFragment() {}

    public interface ItemClickHelper {
        void onUpdateItemClick(BoardUpdateItemModel itemModel, String columnTitle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBoardDetailColumnBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(getActivity()).get(BoardDetailItemViewModel.class);
        projectActivityViewModel = new ViewModelProvider(getActivity()).get(ProjectActivityViewModel.class);

        String projectId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.CURRENT_PROJECT_ID);
        if(projectId == null) {
            ToastUtils.showToastError(getContext(), "Something went wrong, please try again", Toast.LENGTH_LONG);
            getActivity().finish();
        }
        projectActivityViewModel.getProjectById(projectId, new ProjectActivityViewModel.ApiCallHandlers() {
            @Override
            public void onSuccess() {
                creatorId = projectActivityViewModel.getProjectModel().getCreatorId();
                listAdminId.addAll(projectActivityViewModel.getProjectModel().getAdminIds());
            }

            @Override
            public void onFailure(String message) {

            }
        });

        adapter = new BoardItemDetailColumnAdapter(viewModel, getActivity(), new BoardItemDetailColumnAdapter.ClickHandlers() {
            @Override
            public void onMapItemClick(BoardMapItemModel itemModel, String columnTitle, int columnPos) {
                Intent mapIntent = new Intent(getActivity(), MapActivity.class);
                String itemJson = new Gson().toJson(itemModel);
                mapIntent.putExtra("itemModel", itemJson);
                mapIntent.putExtra("projectId", viewModel.getProjectId());
                mapIntent.putExtra("boardId", viewModel.getBoardId());
                mapIntent.putExtra("cellId", itemModel.get_id());
                startActivity(mapIntent);
            }

            @Override
            public void onUpdateItemClick(BoardUpdateItemModel itemModel, String columnTitle) {
                ((ItemClickHelper) getActivity()).onUpdateItemClick(itemModel, columnTitle);
            }

            @Override
            public void onCheckboxItemClick(BoardCheckboxItemModel itemModel, int columnPos) {
                onCheckboxItemClicked(itemModel, columnPos);
            }

            @Override
            public void OnDateItemClick(BoardDateItemModel itemModel, String columnTitle, int columnPos) {
                showDateItemPopup(itemModel, columnTitle, columnPos);
            }

            @Override
            public void onNumberItemClick(BoardNumberItemModel itemModel, String columnTitle, int columnPos) {
                showNumberItemPopup(itemModel, columnTitle, columnPos);
            }

            @Override
            public void onStatusItemClick(BoardStatusItemModel itemModel, int columnPos) {
                showTaskStatusPopup(itemModel, columnPos);
            }

            @Override
            public void onTextItemClick(BoardTextItemModel itemModel, String columnTitle, int columnPos) {
                showTextItemPopup(itemModel, columnTitle, columnPos);
            }

            @Override
            public void OnTimelineItemClick(BoardTimelineItemModel itemModel, String columnTitle, int columnPos) {
                showTimelineItemPopup(itemModel, columnTitle, columnPos);
            }

            @Override
            public void onUserItemClick(BoardUserItemModel userItemModel, String columnTitle, int columnPosition) {
                showOwnerPopup(userItemModel, columnTitle, columnPosition);
            }
        });

        binding.rvItemContent.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvItemContent.setHasFixedSize(true);
        binding.rvItemContent.setAdapter(adapter);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        binding.rvItemContent.addItemDecoration(itemDecorator);

        return binding.getRoot();
    }


    private void showOwnerPopup(BoardUserItemModel userItemModel, String columnTitle, int columnPos) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardOwnerItemPopupBinding binding = BoardOwnerItemPopupBinding.inflate(getLayoutInflater());
        binding.popupTitle.setText(columnTitle);
        dialog.setContentView(binding.getRoot());
        List<UserModel> listMember = new ArrayList<>();
        List<UserModel> listOwner = new ArrayList<>(userItemModel.getUsers());
        binding.rvMembers.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvOwers.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        BoardItemOwnerAdapter boardItemOwnerAdapter;
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        if(creatorId.equals(userId))
            boardItemOwnerAdapter = new BoardItemOwnerAdapter(listOwner, false);
        else if (listAdminId.contains(userId))
            boardItemOwnerAdapter = new BoardItemOwnerAdapter(listOwner, false);
        else
            boardItemOwnerAdapter = new BoardItemOwnerAdapter(listOwner, true);
        BoardItemMemberAdapter boardItemMemberAdapter = new BoardItemMemberAdapter();
        binding.rvMembers.setAdapter(boardItemMemberAdapter);
        binding.rvOwers.setAdapter(boardItemOwnerAdapter);

        viewModel.getMember(new BoardDetailItemViewModel.MemberApiCallHandler() {
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

                String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
                if(creatorId.equals(userId))
                    showPopupOwnerFor(Role.CREATOR, binding);
                else if (listAdminId.contains(userId))
                    showPopupOwnerFor(Role.ADMIN, binding);
                else
                    showPopupOwnerFor(Role.MEMBER, binding);

            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(getActivity(), message, Toast.LENGTH_SHORT);
                dialog.dismiss();
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
            viewModel.updateACell(userItemModel).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        adapter.notifyItemChanged(columnPos);
                        ToastUtils.showToastSuccess(getActivity(), "Updated", Toast.LENGTH_SHORT);
                    } else ToastUtils.showToastError(getActivity(), response.message(), Toast.LENGTH_SHORT);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    ToastUtils.showToastError(getActivity(), t.getMessage(), Toast.LENGTH_SHORT);
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

    private void showTextItemPopup(BoardTextItemModel itemModel, String title, int columnPos) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardTextItemPopupBinding binding = BoardTextItemPopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        binding.textItemTitle.setText(title);
        binding.etTextItem.setText(itemModel.getContent());
        binding.btnClosePopup.setOnClickListener((view) -> dialog.dismiss());

        binding.btnSaveTextItem.setOnClickListener(view -> {
            String newContent = String.valueOf(binding.etTextItem.getText());
            itemModel.setContent(newContent);

            Dialog loadingDialog = DialogUtils.GetLoadingDialog(getActivity());
            loadingDialog.show();
            viewModel.updateACell(itemModel).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        adapter.notifyItemChanged(columnPos);
                    } else {
                        ToastUtils.showToastError(getActivity(), "Unable to update the cell", Toast.LENGTH_LONG);
                    }

                    loadingDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    ToastUtils.showToastError(getActivity(), "Unable to update the cell", Toast.LENGTH_LONG);
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

    private void showTaskStatusPopup(BoardStatusItemModel statusItemModel, int columnPos)
    {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardStatusItemPopupBinding binding = BoardStatusItemPopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        StatusContentsAdapter statusContentsAdapter = new StatusContentsAdapter(statusItemModel);
        statusContentsAdapter.setHandlers((itemModel, newContent) -> {
            itemModel.setContent(newContent);
            Dialog loadingDialog = DialogUtils.GetLoadingDialog(getActivity());
            loadingDialog.show();
            viewModel.updateACell(itemModel).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        adapter.notifyItemChanged(columnPos);
                    } else {
                        ToastUtils.showToastError(getActivity(), "Unable to save the cell", Toast.LENGTH_LONG);
                    }

                    loadingDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    ToastUtils.showToastError(getActivity(), "Unable to save the cell", Toast.LENGTH_LONG);
                    loadingDialog.dismiss();
                    dialog.dismiss();
                }
            });
        });
        binding.rvStatusContents.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvStatusContents.setAdapter(statusContentsAdapter);

        binding.btnClose.setOnClickListener(view -> dialog.dismiss());
        binding.btnEditLabels.setOnClickListener(view -> {
            showStatusContentsEdit(statusItemModel, statusContentsAdapter, columnPos);
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void showStatusContentsEdit(BoardStatusItemModel statusItemModel, StatusContentsAdapter statusContentsAdapter, int columnPos) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardStatusEditViewBinding binding = BoardStatusEditViewBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        BoardStatusItemModel clonedItemModel = new BoardStatusItemModel(statusItemModel);

        StatusEditItemAdapter statusEditItemAdapter = new StatusEditItemAdapter(clonedItemModel);
        statusEditItemAdapter.setHandlers(new StatusEditItemAdapter.ClickHandlers() {
            @Override
            public void onChooseColorClick(int position, BoardStatusItemModel itemModel) {
                new ColorPickerDialog.Builder(getActivity())
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

        binding.rvStatusItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.rvStatusItems.setAdapter(statusEditItemAdapter);
        binding.btnAdd.setOnClickListener(view -> {
            showAddNewStatusDialog(clonedItemModel, statusEditItemAdapter);
        });

        binding.btnClose.setOnClickListener(view -> dialog.dismiss());
        binding.btnSave.setOnClickListener(view -> {
            statusItemModel.copyDataFromAnotherInstance(clonedItemModel);

            Dialog loadingDialog = DialogUtils.GetLoadingDialog(getActivity());
            loadingDialog.show();
            viewModel.updateACell(statusItemModel).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        adapter.notifyItemChanged(columnPos);
                        statusContentsAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtils.showToastError(getActivity(), "Unable to save", Toast.LENGTH_LONG);
                    }

                    loadingDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    ToastUtils.showToastError(getActivity(), "Unable to save", Toast.LENGTH_LONG);
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
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardStatusEditNewItemPopupBinding binding = BoardStatusEditNewItemPopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        binding.btnClosePopup.setOnClickListener((view) -> dialog.dismiss());
        binding.btnAdd.setOnClickListener(view -> {
            String newContent = binding.etTextItem.getText().toString();
            for (int i = 0; i < itemModel.getContents().size(); i++) {
                if (itemModel.getContents().get(i).equals(newContent)) {
                    ToastUtils.showToastError(getActivity(), "Already existed", Toast.LENGTH_LONG);
                    return;
                }
            }
            itemModel.addNewContent(newContent);
            statusEditItemAdapter.notifyItemInserted(itemModel.getContents().size());
            dialog.dismiss();
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }

    private void showDateItemPopup(BoardDateItemModel itemModel, String columnTitle, int columnPos) {
        final Dialog dialog = new Dialog(getActivity());
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

        binding.tvDateItemTitle.setText(columnTitle);
        binding.btnClosePopup.setOnClickListener((view) -> dialog.dismiss());
        binding.btnSaveDateItem.setOnClickListener(view -> {
            itemModel.setYear(dialogYear.get());
            itemModel.setMonth(dialogMonth.get());
            itemModel.setDay(dialogDay.get());
            itemModel.setHour(dialogHour.get());
            itemModel.setMinute(dialogMinute.get());

            Dialog loadingDialog = DialogUtils.GetLoadingDialog(getActivity());
            loadingDialog.show();
            Call<Void> cellUpdateCall = viewModel.updateACell(itemModel);
            cellUpdateCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        adapter.notifyItemChanged(columnPos);
                    } else {
                        ToastUtils.showToastError(getActivity(), "Unable to update the cell", Toast.LENGTH_LONG);
                    }

                    loadingDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    ToastUtils.showToastError(getActivity(), "Unable to update the cell", Toast.LENGTH_LONG);
                    loadingDialog.dismiss();
                    dialog.dismiss();
                }
            });

        });

        binding.dateItemDateContainer.setOnClickListener((view) -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getActivity(),
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
            new TimePickerDialog(getActivity(),
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

    private void onCheckboxItemClicked(BoardCheckboxItemModel itemModel, int columnPosition) {
        Dialog loadingDialog = DialogUtils.GetLoadingDialog(getActivity());
        loadingDialog.show();
        itemModel.setChecked(!itemModel.getChecked());
        viewModel.updateACell(itemModel).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    adapter.notifyItemChanged(columnPosition);
                } else {
                    ToastUtils.showToastError(getActivity(), "Unable to update the cell", Toast.LENGTH_LONG);
                }

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                ToastUtils.showToastError(getActivity(), "Unable to update the cell", Toast.LENGTH_LONG);
                loadingDialog.dismiss();
            }
        });
    }

    private void showTimelineItemPopup(BoardTimelineItemModel itemModel, String title, int columnPos) {
        final Dialog dialog = new Dialog(getActivity());
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
            Dialog loadingDialog = DialogUtils.GetLoadingDialog(getActivity());
            loadingDialog.show();

            // TODO: The function expects no problems or exceptions, should not update the item if the call failed
            itemModel.setStartYear(dialogStartYear.get());
            itemModel.setStartMonth(dialogStartMonth.get());
            itemModel.setStartDay(dialogStartDay.get());
            itemModel.setEndYear(dialogEndYear.get());
            itemModel.setEndMonth(dialogEndMonth.get());
            itemModel.setEndDay(dialogEndDay.get());

            viewModel.updateACell(itemModel).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        adapter.notifyItemChanged(columnPos);
                    } else {
                        ToastUtils.showToastError(getActivity(), "Unable to save the cell", Toast.LENGTH_LONG);
                    }
                    loadingDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    ToastUtils.showToastError(getActivity(), "Unable to save the cell", Toast.LENGTH_LONG);
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

            materialDatePicker.show(getParentFragmentManager(), "I DONT KNOW WHAT THIS IS");
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void showNumberItemPopup(BoardNumberItemModel itemModel, String title, int columnPos) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        BoardNumberItemPopupBinding binding = BoardNumberItemPopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        binding.textNumberTitle.setText(title);
        binding.etNumberItem.setText(itemModel.getContent());
        binding.btnClosePopup.setOnClickListener((view) -> dialog.dismiss());
        binding.btnSaveNumberItem.setOnClickListener(view -> {
            String newContent = String.valueOf(binding.etNumberItem.getText());
            itemModel.setContent(newContent);

            Dialog loadingDialog = DialogUtils.GetLoadingDialog(getActivity());
            loadingDialog.show();
            viewModel.updateACell(itemModel).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        adapter.notifyItemChanged(columnPos);
                    } else {
                        ToastUtils.showToastError(getActivity(), "Unable to update the cell", Toast.LENGTH_LONG);
                    }

                    loadingDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    ToastUtils.showToastError(getActivity(), "Unable to update the cell", Toast.LENGTH_LONG);
                    loadingDialog.dismiss();
                    dialog.dismiss();
                }
            });

            adapter.notifyItemChanged(columnPos);
            dialog.dismiss();
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private enum Role {CREATOR, ADMIN, MEMBER}
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
}