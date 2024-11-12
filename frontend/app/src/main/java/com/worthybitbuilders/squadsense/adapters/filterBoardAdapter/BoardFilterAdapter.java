package com.worthybitbuilders.squadsense.adapters.filterBoardAdapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.databinding.FilterBoardItemBinding;
import com.worthybitbuilders.squadsense.models.FilterModel;
import com.worthybitbuilders.squadsense.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class BoardFilterAdapter extends RecyclerView.Adapter {
    private List<FilterModel> listFilterName;
    private List<List<String>> listFilterCollection;
    private List<List<String>> listSelectedCollection;
    public BoardFilterAdapter(List<FilterModel> listFilterName, List<List<String>> listFilterCollection, List<List<String>> listSelectedCollection) {
        this.listFilterName = listFilterName;
        this.listFilterCollection = listFilterCollection;
        this.listSelectedCollection = listSelectedCollection;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FilterBoardItemBinding binding = FilterBoardItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BoardFilterHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FilterModel filterName = (FilterModel) listFilterName.get(position);
        List<String> filterCollection;
        List<String> selectedCollection;

        if(listFilterCollection != null)
        {
            if(position < listFilterCollection.size())
                filterCollection = listFilterCollection.get(position);
            else{
                filterCollection = new ArrayList<>();
                listSelectedCollection.add(filterCollection);
            }
        }
        else filterCollection = new ArrayList<>();

        if(listSelectedCollection != null )
        {
            if(position < listSelectedCollection.size())
                selectedCollection = listSelectedCollection.get(position);
            else{
                selectedCollection = new ArrayList<>();
                listSelectedCollection.add(selectedCollection);
            }
        }
        else selectedCollection = new ArrayList<>();

        ((BoardFilterHolder) holder).bind(filterName, filterCollection, selectedCollection, position);
    }

    @Override
    public int getItemCount() {
        return listFilterName.size();
    }

    private class BoardFilterHolder extends RecyclerView.ViewHolder {
        FilterBoardItemBinding binding;
        BoardFilterHolder(@NonNull FilterBoardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(FilterModel filter, List<String> filterCollection, List<String> listSelectedItem, int position) {
            binding.filterTitle.setText(filter.getValue());
            binding.rvFilterItem.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));

            if(filter.getType() == FilterModel.TypeFilter.TEXT)
            {
                BoardFilterTextAdapter adapter = new BoardFilterTextAdapter(filterCollection, listSelectedItem);
                binding.rvFilterItem.setAdapter(adapter);
            }
            else
            {
                BoardFilterAvatarAdapter adapter = new BoardFilterAvatarAdapter(filterCollection, listSelectedItem);
                binding.rvFilterItem.setAdapter(adapter);
            }
        }
    }
}