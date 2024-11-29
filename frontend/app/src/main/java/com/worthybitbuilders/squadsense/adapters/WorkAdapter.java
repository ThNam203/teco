package com.worthybitbuilders.squadsense.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.WorkModel;
import com.worthybitbuilders.squadsense.utils.ConvertUtils;

import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        public ImageView doneTick;
        public WorkViewHolder(@NonNull View itemView) {
            super(itemView);
            rowTitle = itemView.findViewById(R.id.tvRowTitle);
            doneTick = itemView.findViewById(R.id.doneTick);
        }

        public void bind(WorkModel workModel) {
            if (workModel.isDone()) doneTick.setVisibility(View.VISIBLE);
            else doneTick.setVisibility(View.GONE);
            rowTitle.setText(String.format(Locale.US, "%s > %s > %s", workModel.getProjectTitle(), workModel.getBoardTitle(), workModel.getRowTitle()));
        }
    }

    public interface ClickHandler {
        void onClick(int position);
    }
}
