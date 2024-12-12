package com.worthybitbuilders.squadsense.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.LifecycleOwner;

import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.adapters.holders.BoardCheckboxItemViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardColumnHeaderViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardDateItemViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardEmptyItemViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardMapItemViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardNumberItemViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardRowHeaderViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardStatusItemViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardTextItemViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardTimelineItemViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardUpdateItemViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardUserItemViewHolder;
import com.worthybitbuilders.squadsense.models.board_models.BoardBaseItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardCheckboxItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardColumnHeaderModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardDateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardMapItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardNumberItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardRowHeaderModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardStatusItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTextItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTimelineItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUpdateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;
import com.worthybitbuilders.squadsense.viewmodels.BoardViewModel;

import kotlin.NotImplementedError;

public class TableViewAdapter extends AbstractTableAdapter<BoardColumnHeaderModel, BoardRowHeaderModel, BoardBaseItemModel> {
    private final BoardViewModel boardViewModel;
    private final Context mContext;
    private final OnClickHandlers handlers;
    private TextView tvBoardTitle;
    public interface OnClickHandlers
            extends BoardStatusItemViewHolder.StatusItemClickHandlers,
                    BoardUserItemViewHolder.UserItemClickHandler,
                    BoardTextItemViewHolder.TextItemClickHandlers,
                    BoardUpdateItemViewHolder.UpdateItemClickHandlers,
                    BoardNumberItemViewHolder.NumberItemClickHandlers,
                    BoardCheckboxItemViewHolder.CheckboxItemClickHandlers,
                    BoardDateItemViewHolder.DateItemClickHandlers,
                    BoardTimelineItemViewHolder.TimelineItemClickHandlers,
                    BoardMapItemViewHolder.MapItemClickHandlers
    {
        void onNewColumnHeaderClick();
        void onNewRowHeaderClick();

        /**
         * These methods are different with ones above,
         * these are for "non new" which is NOT the one to add a new row or column
         */
        void onRowHeaderClick(int rowPosition, String rowTitle);
        void onColumnHeaderClick(BoardColumnHeaderModel headerModel, int columnPosition, View anchor);
    }

    public TableViewAdapter(Context context, BoardViewModel boardViewModel, OnClickHandlers handlers) {
        this.mContext = context;
        this.boardViewModel = boardViewModel;
        this.handlers = handlers;

        boardViewModel.getCellLiveData().observe((LifecycleOwner) mContext, lists -> {
            if (lists == null) return;
            setCellItems(lists);
            notifyDataSetChanged();
        });

        boardViewModel.getColumnLiveData().observe((LifecycleOwner) mContext, boardColumnHeaderModels -> {
            if (boardColumnHeaderModels == null) return;
            setColumnHeaderItems(boardColumnHeaderModels);
            notifyDataSetChanged();
        });

        boardViewModel.getRowLiveData().observe((LifecycleOwner) mContext, boardRowHeaderModels -> {
            if (boardRowHeaderModels == null) return;
            setRowHeaderItems(boardRowHeaderModels);
            notifyDataSetChanged();
        });

        boardViewModel.getBoardTitleLiveData().observe((LifecycleOwner) mContext, newTitle -> {
            if (newTitle != null && !newTitle.isEmpty() && this.tvBoardTitle != null) this.tvBoardTitle.setText(newTitle);
        });
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateCellViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == BoardColumnHeaderModel.ColumnType.Status.getKey()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_item_status, parent, false);
            return new BoardStatusItemViewHolder(view, handlers);
        } else if (viewType == BoardColumnHeaderModel.ColumnType.Text.getKey()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_item_text, parent, false);
            return new BoardTextItemViewHolder(view, handlers);
        } else if (viewType == BoardColumnHeaderModel.ColumnType.User.getKey()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_item_user, parent, false);
            return new BoardUserItemViewHolder(view, handlers);
        } else if (viewType == BoardColumnHeaderModel.ColumnType.NewColumn.getKey()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_item_empty, parent, false);
            return new BoardEmptyItemViewHolder(view);
        } else if (viewType == BoardColumnHeaderModel.ColumnType.Number.getKey()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_item_number, parent, false);
            return new BoardNumberItemViewHolder(view, handlers);
        } else if (viewType == BoardColumnHeaderModel.ColumnType.Update.getKey()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_item_update, parent, false);
            return new BoardUpdateItemViewHolder(view, handlers);
        } else if (viewType == BoardColumnHeaderModel.ColumnType.Checkbox.getKey()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_item_checkbox, parent, false);
            return new BoardCheckboxItemViewHolder(view, handlers);
        } else if (viewType == BoardColumnHeaderModel.ColumnType.Date.getKey()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_item_date, parent, false);
            return new BoardDateItemViewHolder(view, handlers);
        } else if (viewType == BoardColumnHeaderModel.ColumnType.TimeLine.getKey()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_item_timeline, parent, false);
            return new BoardTimelineItemViewHolder(view, handlers);
        } else if (viewType == BoardColumnHeaderModel.ColumnType.Map.getKey()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_item_map, parent, false);
            return new BoardMapItemViewHolder(view, handlers);
        }

        throw new NotImplementedError();
    }

    @Override
    public void onBindCellViewHolder(@NonNull AbstractViewHolder holder, @Nullable BoardBaseItemModel cellItemModel, int columnPosition, int rowPosition) {
        if (cellItemModel == null) return;
        String columnTitle = mColumnHeaderItems.get(columnPosition).getTitle();
        String rowTitle = mRowHeaderItems.get(rowPosition).getTitle();
        boolean isReadOnly = boardViewModel.getmRowHeaderModelList().get(rowPosition).isDone();
        // creator admin owner
        // new thieu projectId creator -> userId, ad
        // exist boardViewmodel
        if (holder instanceof BoardStatusItemViewHolder) {
            ((BoardStatusItemViewHolder) holder).setItemModel((BoardStatusItemModel) cellItemModel, columnTitle, columnPosition, rowPosition);
        } else if (holder instanceof BoardTextItemViewHolder) {
            ((BoardTextItemViewHolder) holder).setItemModel((BoardTextItemModel) cellItemModel, columnTitle, columnPosition, rowPosition);
        } else if (holder instanceof BoardUserItemViewHolder) {
            ((BoardUserItemViewHolder) holder).setItemModel((BoardUserItemModel) cellItemModel, mContext, columnTitle, columnPosition, rowPosition);
        } else if (holder instanceof BoardUpdateItemViewHolder) {
            ((BoardUpdateItemViewHolder) holder).setItemModel((BoardUpdateItemModel) cellItemModel, rowPosition, rowTitle, columnTitle);
        } else if (holder instanceof BoardNumberItemViewHolder) {
            ((BoardNumberItemViewHolder) holder).setItemModel((BoardNumberItemModel) cellItemModel, columnTitle, columnPosition, rowPosition);
        } else if (holder instanceof BoardCheckboxItemViewHolder) {
            ((BoardCheckboxItemViewHolder) holder).setItemModel((BoardCheckboxItemModel) cellItemModel, columnPosition, rowPosition, isReadOnly);
        } else if (holder instanceof BoardDateItemViewHolder) {
            ((BoardDateItemViewHolder) holder).setItemModel((BoardDateItemModel) cellItemModel, columnTitle, columnPosition, rowPosition, boardViewModel.getDeadlineColumnIndex());
        } else if (holder instanceof BoardTimelineItemViewHolder) {
            ((BoardTimelineItemViewHolder) holder).setItemModel((BoardTimelineItemModel) cellItemModel, columnTitle, columnPosition, rowPosition, boardViewModel.getDeadlineColumnIndex());
        } else if (holder instanceof BoardMapItemViewHolder) {
            ((BoardMapItemViewHolder) holder).setItemModel((BoardMapItemModel) cellItemModel, columnTitle, columnPosition, rowPosition);
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_column_header_view, parent, false);
        return new BoardColumnHeaderViewHolder(layout);
    }

    @Override
    public void onBindColumnHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable BoardColumnHeaderModel columnHeaderItemModel, int columnPosition) {
        BoardColumnHeaderViewHolder columnHolder = (BoardColumnHeaderViewHolder) holder;
        if (columnHeaderItemModel == null) return;

        // the column title can be too long, we must manually resize the column
        if (columnHeaderItemModel.getColumnType() == BoardColumnHeaderModel.ColumnType.Status) {
            columnHolder.itemView.setLayoutParams(new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.MATCH_PARENT));
        } else if (columnHeaderItemModel.getColumnType() == BoardColumnHeaderModel.ColumnType.Text) {
            columnHolder.itemView.setLayoutParams(new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.MATCH_PARENT));
        } else if (columnHeaderItemModel.getColumnType() == BoardColumnHeaderModel.ColumnType.User) {
            columnHolder.itemView.setLayoutParams(new LinearLayout.LayoutParams(80, LinearLayout.LayoutParams.MATCH_PARENT));
        } else if (columnHeaderItemModel.getColumnType() == BoardColumnHeaderModel.ColumnType.NewColumn) {
            columnHolder.itemView.setLayoutParams(new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.MATCH_PARENT));
        } else if (columnHeaderItemModel.getColumnType() == BoardColumnHeaderModel.ColumnType.Number) {
            columnHolder.itemView.setLayoutParams(new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.MATCH_PARENT));
        } else if (columnHeaderItemModel.getColumnType() == BoardColumnHeaderModel.ColumnType.Update) {
            columnHolder.itemView.setLayoutParams(new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.MATCH_PARENT));
        } else if (columnHeaderItemModel.getColumnType() == BoardColumnHeaderModel.ColumnType.Checkbox) {
            columnHolder.itemView.setLayoutParams(new LinearLayout.LayoutParams(80, LinearLayout.LayoutParams.MATCH_PARENT));
        } else if (columnHeaderItemModel.getColumnType() == BoardColumnHeaderModel.ColumnType.Date) {
            columnHolder.itemView.setLayoutParams(new LinearLayout.LayoutParams(180, LinearLayout.LayoutParams.MATCH_PARENT));
        } else if (columnHeaderItemModel.getColumnType() == BoardColumnHeaderModel.ColumnType.TimeLine) {
            columnHolder.itemView.setLayoutParams(new LinearLayout.LayoutParams(180, LinearLayout.LayoutParams.MATCH_PARENT));
        } else if (columnHeaderItemModel.getColumnType() == BoardColumnHeaderModel.ColumnType.Map) {
            columnHolder.itemView.setLayoutParams(new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.MATCH_PARENT));
        }

        if (columnHeaderItemModel.getColumnType() == BoardColumnHeaderModel.ColumnType.NewColumn)
            holder.itemView.setOnClickListener((view) -> handlers.onNewColumnHeaderClick());
        else holder.itemView.setOnClickListener((view) -> handlers.onColumnHeaderClick(columnHeaderItemModel, columnPosition, holder.itemView));
        columnHolder.setColumnHeaderModel(columnHeaderItemModel);
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.board_row_header_view, parent, false);
        return new BoardRowHeaderViewHolder(layout);
    }

    @Override
    public void onBindRowHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable BoardRowHeaderModel rowHeaderItemModel, int rowPosition) {
        BoardRowHeaderViewHolder headerHolder = (BoardRowHeaderViewHolder) holder;
        if (rowHeaderItemModel == null) return;
        headerHolder.setRowHeaderModel(rowHeaderItemModel);

        if (rowHeaderItemModel.getIsAddNewRowRow() != null && rowHeaderItemModel.getIsAddNewRowRow()) {
            headerHolder.itemView.setOnClickListener(view -> handlers.onNewRowHeaderClick());
            headerHolder.container.setBackground(ContextCompat.getDrawable(mContext, R.drawable.background_board_new_row_header));
        }
        else headerHolder.itemView.setOnClickListener(view -> handlers.onRowHeaderClick(rowPosition, rowHeaderItemModel.getTitle()));
    }

    @NonNull
    @Override
    public View onCreateCornerView(@NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_corner_view, parent, false);
        this.tvBoardTitle = view.findViewById(R.id.tvBoardTitle);
        this.tvBoardTitle.setText(boardViewModel.getBoardTitle());

        return view;
    }

    @Override
    public int getCellItemViewType(int columnPosition) {
        return boardViewModel.getCellItemViewType(columnPosition);
    }

    @Override
    public int getColumnHeaderItemViewType(int columnPosition) {
        return boardViewModel.getCellItemViewType(columnPosition);
    }
}