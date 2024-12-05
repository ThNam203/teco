package com.worthybitbuilders.squadsense.adapters.holders;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardStatusItemModel;

import java.util.Objects;

public class BoardStatusItemViewHolder extends AbstractViewHolder {
    public final TextView taskStatusContent;
    private final StatusItemClickHandlers handlers;

    public BoardStatusItemViewHolder(@NonNull View itemView, StatusItemClickHandlers handlers) {
        super(itemView);
        this.taskStatusContent = itemView.findViewById(R.id.tvTaskStatusContent);
        this.handlers = handlers;
    }

    public void setItemModel(BoardStatusItemModel itemModel, String columnTitle, int columnPos, int rowPos) {
        taskStatusContent.setText(itemModel.getContent());

        String color = null;
        for (int i = 0; i < itemModel.getContents().size(); i++) {
            if (Objects.equals(itemModel.getContent(), itemModel.getContents().get(i))) color = itemModel.getColorAt(i);
        }
        if (color == null) DrawableCompat.setTint(taskStatusContent.getBackground(), Color.parseColor("#9c9c9c"));
        else DrawableCompat.setTint(taskStatusContent.getBackground(), Color.parseColor(color));
        itemView.setOnClickListener((view) -> {
            handlers.onStatusItemClick(itemModel, columnTitle, columnPos, rowPos);
        });
    }

    public interface StatusItemClickHandlers {
        void onStatusItemClick(BoardStatusItemModel itemModel, String columnTitle, int columnPos, int rowPos);
    }
}
