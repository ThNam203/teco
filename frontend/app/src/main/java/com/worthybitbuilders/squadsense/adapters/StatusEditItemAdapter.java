package com.worthybitbuilders.squadsense.adapters;

import android.graphics.Color;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardStatusItemModel;

import java.util.List;

public class StatusEditItemAdapter extends RecyclerView.Adapter {
    private final BoardStatusItemModel itemModel;
    private ClickHandlers handlers;

    public StatusEditItemAdapter(BoardStatusItemModel itemModel) {
        this.itemModel = itemModel;
    }

    public void setHandlers(ClickHandlers handlers) {
        this.handlers = handlers;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.board_status_edit_item_view, parent, false);
        return new StatusEditItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((StatusEditItemViewHolder) holder).bind(position, itemModel, handlers);
    }

    @Override
    public int getItemCount() {
        return this.itemModel.getContents().size();
    }

    private static class StatusEditItemViewHolder extends RecyclerView.ViewHolder {
        private final EditText etContent;
        private final ImageButton btnChooseColor;
        private final ImageButton btnDelete;

        public StatusEditItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.etContent = itemView.findViewById(R.id.etText);
            this.btnChooseColor = itemView.findViewById(R.id.btnChooseColor);
            this.btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(int position, BoardStatusItemModel itemModel, ClickHandlers handlers) {
            btnChooseColor.setOnClickListener(view -> handlers.onChooseColorClick(position, itemModel));
            btnDelete.setOnClickListener(view -> handlers.onDeleteClick(position, itemModel));
            etContent.setBackgroundColor(Color.parseColor(itemModel.getColorAt(position)));
            etContent.setText(itemModel.getContents().get(position));

            etContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // TODO: IF USER EDIT THE ITEM THAT HAS ALREADY BEEN EXISTED, WE SHOULD STOP IT
                    if (itemModel.getContent().equals(itemModel.getContents().get(position)))
                        itemModel.setContent(charSequence.toString());
                    itemModel.setContentAt(position, charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
    }

    public interface ClickHandlers {
        void onChooseColorClick(int position, BoardStatusItemModel itemModel);
        void onDeleteClick(int position, BoardStatusItemModel itemModel);
    }
}
