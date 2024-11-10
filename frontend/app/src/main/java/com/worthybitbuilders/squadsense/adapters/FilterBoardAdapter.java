package com.worthybitbuilders.squadsense.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ItemFilterBoardBinding;
import com.worthybitbuilders.squadsense.databinding.ItemFilterDateBinding;
import com.worthybitbuilders.squadsense.models.ActivityLog;

import java.util.List;

public class FilterBoardAdapter extends RecyclerView.Adapter {
    private List<ActivityLog.ActivityLogBoard> listBoard;
    private List<ActivityLog.ActivityLogBoard> listSelectedBoard;

    public FilterBoardAdapter(List<ActivityLog.ActivityLogBoard> listBoard, List<ActivityLog.ActivityLogBoard> listSelectedBoard) {
        this.listBoard = listBoard;
        this.listSelectedBoard = listSelectedBoard;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFilterBoardBinding binding = ItemFilterBoardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FilterBoardHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ActivityLog.ActivityLogBoard board = (ActivityLog.ActivityLogBoard) listBoard.get(position);
        ((FilterBoardHolder) holder).bind(board, position);
    }


    @Override
    public int getItemCount() {
        return listBoard.size();
    }

    private class FilterBoardHolder extends RecyclerView.ViewHolder {
        ItemFilterBoardBinding binding;
        FilterBoardHolder(@NonNull ItemFilterBoardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ActivityLog.ActivityLogBoard board, int position) {
            binding.tvBoardName.setText(board.boardTitle);

            ChangeBackgroundOnSelected(board);

            itemView.setOnClickListener(view -> {
                if(listSelectedBoard.contains(board)) listSelectedBoard.remove(board);
                else listSelectedBoard.add(board);
                ChangeBackgroundOnSelected(board);
            });
        }
        private void ChangeBackgroundOnSelected(ActivityLog.ActivityLogBoard board)
        {
            if(listSelectedBoard.contains(board))
            {
                int color = ContextCompat.getColor(itemView.getContext(), R.color.white);
                binding.tvBoardName.setTextColor(color);
                int bgColor = ContextCompat.getColor(itemView.getContext(), R.color.blue);
                binding.tvBoardName.setBackgroundTintList(ColorStateList.valueOf(bgColor));
            }
            else
            {
                int color = ContextCompat.getColor(itemView.getContext(), R.color.primary_word_color);
                binding.tvBoardName.setTextColor(color);
                int bgColor = ContextCompat.getColor(itemView.getContext(), R.color.primary_second_color);
                binding.tvBoardName.setBackgroundTintList(ColorStateList.valueOf(bgColor));
            }
        }
    }
}