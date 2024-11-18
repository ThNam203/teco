package com.worthybitbuilders.squadsense.adapters.holders;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardStatusItemModel;

import java.util.Objects;

public class BoardDetailStatusItemViewHolder extends AbstractViewHolder {
    private final TextView taskStatusContent;
    private final TextView tvTitleColumn;
    private final StatusItemClickHandlers handlers;

    private Context context;

    public BoardDetailStatusItemViewHolder(@NonNull View itemView, Context context, StatusItemClickHandlers handlers) {
        super(itemView);
        this.taskStatusContent = itemView.findViewById(R.id.tvTaskStatusContent);
        this.tvTitleColumn = itemView.findViewById(R.id.tvColumnTitle);
        this.handlers = handlers;
        this.context = context;
    }

    public void setItemModel(BoardStatusItemModel itemModel, String columnTitle, int columnPos) {
        taskStatusContent.setText(itemModel.getContent());
        this.tvTitleColumn.setText(columnTitle);

        String color = null;
        for (int i = 0; i < itemModel.getContents().size(); i++) {
            if (Objects.equals(itemModel.getContent(), itemModel.getContents().get(i))) color = itemModel.getColorAt(i);
        }
        if (color == null) {
            int colorTint = ContextCompat.getColor(context, R.color.primary_second_color);
            DrawableCompat.setTint(taskStatusContent.getBackground(), colorTint);
        }
        else DrawableCompat.setTint(taskStatusContent.getBackground(), Color.parseColor(color));

        taskStatusContent.setOnClickListener((view) -> {
            handlers.onStatusItemClick(itemModel, columnPos);
        });
    }

    public interface StatusItemClickHandlers {
        void onStatusItemClick(BoardStatusItemModel itemModel, int columnPos);
    }
}
