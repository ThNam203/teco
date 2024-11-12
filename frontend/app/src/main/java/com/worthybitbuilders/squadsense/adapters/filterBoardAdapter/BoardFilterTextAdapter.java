package com.worthybitbuilders.squadsense.adapters.filterBoardAdapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ItemBoardFilterTextBinding;
import com.worthybitbuilders.squadsense.databinding.ItemFilterDateBinding;

import java.util.List;

public class BoardFilterTextAdapter extends RecyclerView.Adapter {
    private List<String> listItem;
    private List<String> listSelectedItem;

    public BoardFilterTextAdapter(List<String> listItem, List<String> listSelectedItem) {
        this.listItem = listItem;
        this.listSelectedItem = listSelectedItem;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBoardFilterTextBinding binding = ItemBoardFilterTextBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FilterTextHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String item = (String) listItem.get(position);
        ((FilterTextHolder) holder).bind(item, position);
    }


    @Override
    public int getItemCount() {
        return listItem.size();
    }

    private class FilterTextHolder extends RecyclerView.ViewHolder {
        ItemBoardFilterTextBinding binding;
        FilterTextHolder(@NonNull ItemBoardFilterTextBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String item, int position) {
            binding.tvTitle.setText(item);

            ChangeBackgroundOnSelected(item);

            itemView.setOnClickListener(view -> {
                if(listSelectedItem.contains(item)) listSelectedItem.remove(item);
                else listSelectedItem.add(item);
                ChangeBackgroundOnSelected(item);
            });
        }
        private void ChangeBackgroundOnSelected(String item)
        {
            if(listSelectedItem.contains(item))
            {
                int color = ContextCompat.getColor(itemView.getContext(), R.color.white);
                binding.tvTitle.setTextColor(color);
                int bgColor = ContextCompat.getColor(itemView.getContext(), R.color.blue);
                binding.tvTitle.setBackgroundTintList(ColorStateList.valueOf(bgColor));
            }
            else
            {
                int color = ContextCompat.getColor(itemView.getContext(), R.color.primary_word_color);
                binding.tvTitle.setTextColor(color);
                int bgColor = ContextCompat.getColor(itemView.getContext(), R.color.primary_second_color);
                binding.tvTitle.setBackgroundTintList(ColorStateList.valueOf(bgColor));
            }
        }
    }
}