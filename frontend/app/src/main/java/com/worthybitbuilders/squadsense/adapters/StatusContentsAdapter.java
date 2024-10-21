package com.worthybitbuilders.squadsense.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardStatusItemModel;

public class StatusContentsAdapter extends RecyclerView.Adapter<StatusContentsAdapter.StatusContentViewHolder> {
    private BoardStatusItemModel itemModel;
    ClickHandlers handlers;

    public StatusContentsAdapter(BoardStatusItemModel itemModel) {
        this.itemModel = itemModel;
    }

    public void setHandlers(ClickHandlers handlers) {
        this.handlers = handlers;
    }

    @NonNull
    @Override
    public StatusContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_status_task_item_view, parent, false);
        return new StatusContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusContentViewHolder holder, int position) {
        holder.bind(itemModel, position, handlers);
    }

    @Override
    public int getItemCount() {
        return itemModel.getContents().size();
    }

    public static class StatusContentViewHolder extends RecyclerView.ViewHolder {
        private TextView content;
        public StatusContentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.content = itemView.findViewById(com.worthybitbuilders.squadsense.R.id.tvStatusItemContent);
        }

        public void bind(BoardStatusItemModel itemModel, int position, ClickHandlers handlers) {
            String content = itemModel.getContents().get(position);
            this.content.setText(content);
            DrawableCompat.setTint(this.content.getBackground(), Color.parseColor(itemModel.getColorAt(position)));
            this.content.setOnClickListener(view -> handlers.onClick(itemModel, content));
        }
    }

    public interface ClickHandlers {
        void onClick(BoardStatusItemModel itemModel, String newContent);
    }
}
