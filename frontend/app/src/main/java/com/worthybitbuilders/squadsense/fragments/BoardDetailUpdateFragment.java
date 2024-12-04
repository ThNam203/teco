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
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.BoardDetailItemViewModel;

public class BoardDetailUpdateFragment extends Fragment {
    private BoardDetailItemViewModel viewModel;
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
        binding = FragmentBoardDetailUpdateBinding.inflate(getLayoutInflater());
        updateCellId = getArguments().getString("updateCellId");
        columnTitle = getArguments().getString("columnTitle");
        rowTitle = getArguments().getString("rowTitle");
        viewModel = new ViewModelProvider(getActivity()).get(BoardDetailItemViewModel.class);

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

        handlerIfTaskDone(isDone);
        return binding.getRoot();
    }

    private void handlerIfTaskDone(boolean isDone)
    {
        if(isDone) binding.writeUpdateContainer.setVisibility(View.GONE);
        else binding.writeUpdateContainer.setVisibility(View.VISIBLE);

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
}