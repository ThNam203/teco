package com.worthybitbuilders.squadsense.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ItemFilterDateBinding;
import com.worthybitbuilders.squadsense.databinding.ItemFilterTypeBinding;

import java.util.ArrayList;
import java.util.List;

public class FilterDateAdapter extends RecyclerView.Adapter {
    private List<String> listDate;
    private List<String> listSelectedDate;

    public FilterDateAdapter(List<String> listDate, List<String> listSelectedDate) {
        this.listDate = listDate;
        this.listSelectedDate = listSelectedDate;
    }

    public List<String> getListSelectedDate() { return listSelectedDate; }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFilterDateBinding binding = ItemFilterDateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FilterDateHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String date = (String) listDate.get(position);
        ((FilterDateHolder) holder).bind(date, position);
    }


    @Override
    public int getItemCount() {
        return listDate.size();
    }

    private class FilterDateHolder extends RecyclerView.ViewHolder {
        ItemFilterDateBinding binding;
        FilterDateHolder(@NonNull ItemFilterDateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String date, int position) {
            binding.tvDate.setText(date);

            ChangeBackgroundOnSelected(date);

            itemView.setOnClickListener(view -> {
                if(listSelectedDate.contains(date)) listSelectedDate.remove(date);
                else listSelectedDate.add(date);
                ChangeBackgroundOnSelected(date);
            });
        }
        private void ChangeBackgroundOnSelected(String date)
        {
            if(listSelectedDate.contains(date))
            {
                int color = ContextCompat.getColor(itemView.getContext(), R.color.white);
                binding.tvDate.setTextColor(color);
                int bgColor = ContextCompat.getColor(itemView.getContext(), R.color.blue);
                binding.tvDate.setBackgroundTintList(ColorStateList.valueOf(bgColor));
            }
            else
            {
                int color = ContextCompat.getColor(itemView.getContext(), R.color.primary_word_color);
                binding.tvDate.setTextColor(color);
                int bgColor = ContextCompat.getColor(itemView.getContext(), R.color.primary_second_color);
                binding.tvDate.setBackgroundTintList(ColorStateList.valueOf(bgColor));
            }
        }
    }
}