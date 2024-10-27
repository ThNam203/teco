package com.worthybitbuilders.squadsense.fragments;

import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.activities.ProjectActivity;
import com.worthybitbuilders.squadsense.adapters.BoardItemDetailColumnAdapter;
import com.worthybitbuilders.squadsense.adapters.StatusContentsAdapter;
import com.worthybitbuilders.squadsense.adapters.StatusEditItemAdapter;
import com.worthybitbuilders.squadsense.databinding.BoardDateItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardNumberItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardStatusEditNewItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardStatusEditViewBinding;
import com.worthybitbuilders.squadsense.databinding.BoardStatusItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardTextItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.BoardTimelineItemPopupBinding;
import com.worthybitbuilders.squadsense.databinding.FragmentBoardDetailColumnBinding;
import com.worthybitbuilders.squadsense.models.board_models.BoardBaseItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardCheckboxItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardDateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardNumberItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardStatusItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTextItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTimelineItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;
import com.worthybitbuilders.squadsense.utils.CustomUtils;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.viewmodels.BoardDetailItemViewModel;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardDetailColumnFragment extends Fragment {
    BoardDetailItemViewModel viewModel;
    private FragmentBoardDetailColumnBinding binding;
    private BoardItemDetailColumnAdapter adapter;
    private String projectId;
    private String boardId;
    public BoardDetailColumnFragment(BoardDetailItemViewModel viewModel, String projectId, String boardId) {
        this.viewModel = viewModel;
        this.projectId = projectId;
        this.boardId = boardId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBoardDetailColumnBinding.inflate(getLayoutInflater());

        adapter = new BoardItemDetailColumnAdapter(viewModel, getActivity(), new BoardItemDetailColumnAdapter.ClickHandlers() {
            @Override
            public void onUpdateItemClick(int columnPos) {

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
            public void onUserItemClick(BoardUserItemModel userItemModel, int columnPosition) {

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
            viewModel.updateACell(itemModel, projectId, boardId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        adapter.notifyItemChanged(columnPos);
                    } else {
                        Toast.makeText(getActivity(), "Unable to update the cell", Toast.LENGTH_LONG).show();
                    }

                    loadingDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getActivity(), "Unable to update the cell", Toast.LENGTH_LONG).show();
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
            viewModel.updateACell(itemModel, projectId, boardId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        adapter.notifyItemChanged(columnPos);
                    } else {
                        Toast.makeText(getActivity(), "Unable to save the cell", Toast.LENGTH_LONG).show();
                    }

                    loadingDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getActivity(), "Unable to save the cell", Toast.LENGTH_LONG).show();
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
            viewModel.updateACell(statusItemModel, projectId, boardId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        adapter.notifyItemChanged(columnPos);
                        statusContentsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "Unable to save", Toast.LENGTH_LONG).show();
                    }

                    loadingDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getActivity(), "Unable to save", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getActivity(), "Already existed", Toast.LENGTH_LONG).show();
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
            Call<Void> cellUpdateCall = viewModel.updateACell(itemModel, projectId, boardId);
            cellUpdateCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        adapter.notifyItemChanged(columnPos);
                    } else {
                        Toast.makeText(getActivity(), "Unable to update the cell", Toast.LENGTH_LONG).show();
                    }

                    loadingDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getActivity(), "Unable to update the cell", Toast.LENGTH_LONG).show();
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
        viewModel.updateACell(itemModel, projectId, boardId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    adapter.notifyItemChanged(columnPosition);
                } else {
                    Toast.makeText(getActivity(), "Unable to update the cell", Toast.LENGTH_LONG).show();
                }

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getActivity(), "Unable to update the cell", Toast.LENGTH_LONG).show();
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

            viewModel.updateACell(itemModel, projectId, boardId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        adapter.notifyItemChanged(columnPos);
                    } else {
                        Toast.makeText(getActivity(), "Unable to save the cell", Toast.LENGTH_LONG).show();
                    }
                    loadingDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getActivity(), "Unable to save the cell", Toast.LENGTH_LONG).show();
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
            viewModel.updateACell(itemModel, projectId, boardId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        adapter.notifyItemChanged(columnPos);
                    } else {
                        Toast.makeText(getActivity(), "Unable to update the cell", Toast.LENGTH_LONG).show();
                    }

                    loadingDialog.dismiss();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getActivity(), "Unable to update the cell", Toast.LENGTH_LONG).show();
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
}