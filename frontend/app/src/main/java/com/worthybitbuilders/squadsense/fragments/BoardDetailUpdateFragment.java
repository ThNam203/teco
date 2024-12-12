package com.worthybitbuilders.squadsense.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.worthybitbuilders.squadsense.activities.NewUpdateTaskActivity;
import com.worthybitbuilders.squadsense.adapters.UpdateTaskAdapter;
import com.worthybitbuilders.squadsense.databinding.FragmentBoardDetailUpdateBinding;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardBaseItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.BoardDetailItemViewModel;
import com.worthybitbuilders.squadsense.viewmodels.ProjectActivityViewModel;

import java.util.List;

public class BoardDetailUpdateFragment extends Fragment {
    private BoardDetailItemViewModel viewModel;
    private ProjectActivityViewModel projectActivityViewModel;
    private FragmentBoardDetailUpdateBinding binding;
    private String updateCellId;
    private String columnTitle;
    private String rowTitle;
    private UpdateTaskAdapter adapter;

    private boolean isDone = false;
    public static BoardDetailUpdateFragment newInstance(String updateCellId, String columnTitle, String rowTitle) {
        Bundle args = new Bundle();
        args.putString("rowTitle", rowTitle);
        args.putString("columnTitle", columnTitle);
        args.putString("updateCellId", updateCellId);
        BoardDetailUpdateFragment fragment = new BoardDetailUpdateFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public BoardDetailUpdateFragment() {}

    public void setDone(boolean isDone){
        this.isDone = isDone;
        if(binding == null) return;
        handlerIfTaskDone(isDone);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //done -> chỉ coi, comment vẫn vào được, giấu ô write comment
        //chưa done -> creator, admin, owner -> all
        // không liên quan -> chỉ coi
        binding = FragmentBoardDetailUpdateBinding.inflate(getLayoutInflater());
        updateCellId = getArguments().getString("updateCellId");
        columnTitle = getArguments().getString("columnTitle");
        rowTitle = getArguments().getString("rowTitle");
        viewModel = new ViewModelProvider(getActivity()).get(BoardDetailItemViewModel.class);
        projectActivityViewModel = new ViewModelProvider(getActivity()).get(ProjectActivityViewModel.class);

        binding.rvUpdates.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvUpdates.setHasFixedSize(true);

        binding.writeUpdateContainer.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), NewUpdateTaskActivity.class);
            intent.putExtra("rowTitle", rowTitle);
            intent.putExtra("columnTitle", columnTitle);
            intent.putExtra("projectTitle", viewModel.getProjectTitle());
            intent.putExtra("boardTitle", viewModel.getBoardTitle());
            intent.putExtra("projectId", viewModel.getProjectId());
            intent.putExtra("boardId", viewModel.getBoardId());
            intent.putExtra("cellId", updateCellId);
            startActivity(intent);
        });

        viewModel.getUpdateTasksLiveData().observe(getViewLifecycleOwner(), updateTasks -> {
            if (updateTasks == null || updateTasks.size() == 0) {
                binding.emptyNotification.setVisibility(View.VISIBLE);
                binding.rvUpdates.setVisibility(View.GONE);
            } else {
                binding.emptyNotification.setVisibility(View.GONE);
                binding.rvUpdates.setVisibility(View.VISIBLE);
                adapter.setData(updateTasks);
            }
        });

        adapter = new UpdateTaskAdapter(getActivity(), viewModel, ()  -> {
            binding.emptyNotification.setVisibility(View.VISIBLE);
            binding.rvUpdates.setVisibility(View.GONE);
        });
        binding.rvUpdates.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvUpdates.setAdapter(adapter);

        projectActivityViewModel.getProjectById(viewModel.getProjectId(), new ProjectActivityViewModel.ApiCallHandlers() {
            @Override
            public void onSuccess() {
                handlerIfTaskDone(isDone);
            }

            @Override
            public void onFailure(String message) {

            }
        });
        return binding.getRoot();
    }

    private void handlerIfTaskDone(boolean isDone)
    {
        if(isDone) binding.writeUpdateContainer.setVisibility(View.GONE);
        else {
            String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
            String creatorId = projectActivityViewModel.getProjectModel().getCreatorId();
            List<String> listAdminId = projectActivityViewModel.getProjectModel().getAdminIds();
            if(creatorId.equals(userId) || listAdminId.contains(userId) || isAnOwnerOfRow(userId)) binding.writeUpdateContainer.setVisibility(View.VISIBLE);
            else binding.writeUpdateContainer.setVisibility(View.GONE);
        }

        if(adapter != null){
            adapter.setReadOnly(isDone);
            binding.rvUpdates.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.getUpdateTasksByCellId(updateCellId, new BoardDetailItemViewModel.ApiCallHandler() {
            @Override
            public void onSuccess() {
                binding.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(String message) {
                if (getActivity() != null)
                    ToastUtils.showToastError(getActivity(), message, Toast.LENGTH_LONG);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private boolean isAnOwnerOfRow(String id)
    {
        if(viewModel.getItemsLiveData().getValue() == null) return false;
        List<BoardBaseItemModel> cells = viewModel.getItemsLiveData().getValue().getCells();

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

        return false;
    }
}