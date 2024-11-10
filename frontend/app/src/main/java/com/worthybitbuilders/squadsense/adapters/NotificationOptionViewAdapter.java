package com.worthybitbuilders.squadsense.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ItemFilterTypeBinding;
import com.worthybitbuilders.squadsense.databinding.ItemNotificationOptionViewBinding;

import java.util.List;

public class NotificationOptionViewAdapter extends RecyclerView.Adapter {
    private List<String> listOption;
    private String selectedOption;

    private OptionViewHandler handler;

    public interface OptionViewHandler {
        void AllNotificationHandler();
        void TodayNotificationHandler();
        void SelectDateNotificationHandler(String selectedDate);
    }

    public void setOnOptionViewHandler(OptionViewHandler handler)
    {
        this.handler = handler;
    }

    public void setSelectedOption(String selectedOption)
    {
        this.selectedOption = selectedOption;
    }

    public NotificationOptionViewAdapter(List<String> listOption, String selectedOption) {
        this.listOption = listOption;
        this.selectedOption = selectedOption;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationOptionViewBinding binding = ItemNotificationOptionViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OptionViewHolder(binding, handler);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String option = (String) listOption.get(position);
        ((OptionViewHolder) holder).bind(option, position);
    }


    @Override
    public int getItemCount() {
        return listOption.size();
    }

    private class OptionViewHolder extends RecyclerView.ViewHolder {
        ItemNotificationOptionViewBinding binding;
        OptionViewHandler handler;
        OptionViewHolder(@NonNull ItemNotificationOptionViewBinding binding, OptionViewHandler handler) {
            super(binding.getRoot());
            this.binding = binding;
            this.handler = handler;
        }

        void bind(String title, int position) {
            binding.title.setText(title);

            ChangeBackgroundOnSelected(title);

            itemView.setOnClickListener(view -> {
                ChangeBackgroundOnSelected(title);
                if(title.equals("All"))
                    handler.AllNotificationHandler();
                else if(title.equals("Today"))
                    handler.TodayNotificationHandler();
                else handler.SelectDateNotificationHandler(title);
            });
        }
        private void ChangeBackgroundOnSelected(String optionView)
        {
            if(selectedOption.equals(optionView))
            {
                int color = ContextCompat.getColor(itemView.getContext(), R.color.white);
                binding.title.setTextColor(color);
                int bgColor = ContextCompat.getColor(itemView.getContext(), R.color.primary_btn_color);
                binding.title.setBackgroundTintList(ColorStateList.valueOf(bgColor));
            }
            else
            {
                int color = ContextCompat.getColor(itemView.getContext(), R.color.primary_word_color);
                binding.title.setTextColor(color);
                int bgColor = ContextCompat.getColor(itemView.getContext(), R.color.primary_second_color);
                binding.title.setBackgroundTintList(ColorStateList.valueOf(bgColor));
            }
        }
    }
}