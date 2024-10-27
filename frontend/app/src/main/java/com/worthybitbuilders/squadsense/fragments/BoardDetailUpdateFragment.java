package com.worthybitbuilders.squadsense.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.activities.NewUpdateTaskActivity;
import com.worthybitbuilders.squadsense.adapters.FileUpdateAdapter;
import com.worthybitbuilders.squadsense.databinding.FragmentBoardDetailColumnBinding;
import com.worthybitbuilders.squadsense.databinding.FragmentBoardDetailUpdateBinding;
import com.worthybitbuilders.squadsense.models.board_models.BoardBaseItemModel;
import com.worthybitbuilders.squadsense.viewmodels.BoardDetailItemViewModel;

import java.util.List;

public class BoardDetailUpdateFragment extends Fragment {
    private BoardDetailItemViewModel viewModel;
    private FragmentBoardDetailUpdateBinding binding;
    private String updateCellId;
    public static BoardDetailUpdateFragment newInstance(String updateCellId) {
        Bundle args = new Bundle();
        args.putString("updateCellId", updateCellId);
        BoardDetailUpdateFragment fragment = new BoardDetailUpdateFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public BoardDetailUpdateFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBoardDetailUpdateBinding.inflate(getLayoutInflater());
        updateCellId = getArguments().getString("updateCellId");
        viewModel = new ViewModelProvider(getActivity()).get(BoardDetailItemViewModel.class);


        binding.rvUpdates.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvUpdates.setHasFixedSize(true);

        binding.writeUpdateContainer.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), NewUpdateTaskActivity.class);
            startActivity(intent);
        });

        return binding.getRoot();
    }
}