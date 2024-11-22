package com.worthybitbuilders.squadsense.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.WorkModel;

import java.util.List;

public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.WorkViewHolder> {
    private List<WorkModel> modelList = null;
    private final ClickHandler clickHandler;

    public WorkAdapter(ClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<WorkModel> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.work_view, parent, false);
        return new WorkViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkViewHolder holder, int position) {
        holder.bind(modelList.get(position));
        holder.rowTitle.setOnClickListener((view) -> clickHandler.onClick(position));
    }

    @Override
    public int getItemCount() {
        if (modelList == null) return 0;
        else return modelList.size();
    }

    public static class WorkViewHolder extends RecyclerView.ViewHolder {
        public TextView rowTitle;
        public WorkViewHolder(@NonNull View itemView) {
            super(itemView);
            rowTitle = itemView.findViewById(R.id.tvRowTitle);
        }

        public void bind(WorkModel workModel) {
            rowTitle.setText(workModel.getRowTitle());
        }
    }

    public interface ClickHandler {
        void onClick(int position);
    }
}
