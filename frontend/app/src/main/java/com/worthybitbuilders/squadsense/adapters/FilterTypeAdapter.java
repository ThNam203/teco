package com.worthybitbuilders.squadsense.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ItemFilterTypeBinding;

import java.util.ArrayList;
import java.util.List;

public class FilterTypeAdapter extends RecyclerView.Adapter {
    private List<String> listType;
    private List<String> listSelectedType;

    public FilterTypeAdapter(List<String> listType, List<String> listSelectedType) {
        this.listType = listType;
        this.listSelectedType = listSelectedType;
    }

    public List<String> getListSelectedType() { return listSelectedType; }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFilterTypeBinding binding = ItemFilterTypeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FilterTypeHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String type = (String) listType.get(position);
        ((FilterTypeHolder) holder).bind(type, position);
    }


    @Override
    public int getItemCount() {
        return listType.size();
    }

    private class FilterTypeHolder extends RecyclerView.ViewHolder {
        ItemFilterTypeBinding binding;
        FilterTypeHolder(@NonNull ItemFilterTypeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String type, int position) {
            binding.tvType.setText(type);

            ChangeBackgroundOnSelected(type);

            itemView.setOnClickListener(view -> {
                if(listSelectedType.contains(type)) listSelectedType.remove(type);
                else listSelectedType.add(type);
                ChangeBackgroundOnSelected(type);
            });
        }
        private void ChangeBackgroundOnSelected(String type)
        {
            if(listSelectedType.contains(type))
            {
                int color = ContextCompat.getColor(itemView.getContext(), R.color.white);
                binding.tvType.setTextColor(color);

                if(type.equals("Remove"))
                    binding.tvType.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e63946")));
                else if(type.equals("New"))
                    binding.tvType.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffca3a")));
                else if(type.equals("Update"))
                    binding.tvType.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#48cae4")));
                else if(type.equals("Change"))
                    binding.tvType.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6930c3")));
            }
            else
            {
                int color = ContextCompat.getColor(itemView.getContext(), R.color.primary_word_color);
                binding.tvType.setTextColor(color);
                int bgColor = ContextCompat.getColor(itemView.getContext(), R.color.primary_second_color);
                binding.tvType.setBackgroundTintList(ColorStateList.valueOf(bgColor));
            }
        }
    }
}