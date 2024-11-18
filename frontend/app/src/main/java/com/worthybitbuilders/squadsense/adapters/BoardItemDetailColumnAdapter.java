package com.worthybitbuilders.squadsense.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.adapters.holders.BoardDetailCheckboxItemViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardDetailDateItemViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardDetailMapItemViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardDetailNumberItemViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardDetailStatusItemViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardDetailTextItemViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardDetailTimelineItemViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardDetailUpdateItemViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardDetailUserItemViewHolder;
import com.worthybitbuilders.squadsense.adapters.holders.BoardMapItemViewHolder;
import com.worthybitbuilders.squadsense.models.BoardDetailItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardBaseItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardCheckboxItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardColumnHeaderModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardDateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardMapItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardNumberItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardStatusItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTextItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTimelineItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUpdateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;
import com.worthybitbuilders.squadsense.viewmodels.BoardDetailItemViewModel;

import kotlin.NotImplementedError;

public class BoardItemDetailColumnAdapter extends RecyclerView.Adapter {
    private BoardDetailItemViewModel viewModel;
    private ClickHandlers clickHandlers;
    private Context mContext;
    public interface ClickHandlers extends
            BoardDetailStatusItemViewHolder.StatusItemClickHandlers,
            BoardDetailTextItemViewHolder.TextItemClickHandlers,
            BoardDetailUserItemViewHolder.UserItemClickHandler,
            BoardDetailNumberItemViewHolder.NumberItemClickHandlers,
            BoardDetailCheckboxItemViewHolder.CheckboxItemClickHandlers,
            BoardDetailDateItemViewHolder.DateItemClickHandlers,
            BoardDetailTimelineItemViewHolder.TimelineItemClickHandlers,
            BoardDetailUpdateItemViewHolder.UpdateItemClickHandlers,
            BoardDetailMapItemViewHolder.MapItemClickHandlers
    {}

    public BoardItemDetailColumnAdapter(BoardDetailItemViewModel viewModel, Context mContext, ClickHandlers clickHandlers) {
        this.viewModel = viewModel;
        this.mContext = mContext;
        this.clickHandlers = clickHandlers;

        viewModel.getItemsLiveData().observe((AppCompatActivity)mContext, boardDetailItemModel -> {
            notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == BoardColumnHeaderModel.ColumnType.Status.getKey()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_detail_item_status, parent, false);
            return new BoardDetailStatusItemViewHolder(view, mContext, clickHandlers);
        } else if (viewType == BoardColumnHeaderModel.ColumnType.Text.getKey()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_detail_item_text, parent, false);
            return new BoardDetailTextItemViewHolder(view, clickHandlers);
        } else if (viewType == BoardColumnHeaderModel.ColumnType.User.getKey()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_detail_item_user, parent, false);
            return new BoardDetailUserItemViewHolder(view, clickHandlers);
        } else if (viewType == BoardColumnHeaderModel.ColumnType.Number.getKey()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_detail_item_number, parent, false);
            return new BoardDetailNumberItemViewHolder(view, clickHandlers);
        } else if (viewType == BoardColumnHeaderModel.ColumnType.Checkbox.getKey()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_detail_item_checkbox, parent, false);
            return new BoardDetailCheckboxItemViewHolder(view, clickHandlers);
        } else if (viewType == BoardColumnHeaderModel.ColumnType.Date.getKey()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_detail_item_date, parent, false);
            return new BoardDetailDateItemViewHolder(view, clickHandlers);
        } else if (viewType == BoardColumnHeaderModel.ColumnType.TimeLine.getKey()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_detail_item_timeline, parent, false);
            return new BoardDetailTimelineItemViewHolder(view, clickHandlers);
        } else if (viewType == BoardColumnHeaderModel.ColumnType.Update.getKey()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_detail_item_update, parent, false);
            return new BoardDetailUpdateItemViewHolder(view, clickHandlers);
        } else if (viewType == BoardColumnHeaderModel.ColumnType.Map.getKey()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_detail_item_map, parent, false);
            return new BoardDetailMapItemViewHolder(view, clickHandlers);
        }

        throw new NotImplementedError();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // although the items are placed vertically
        // it is the columnIndex not rowIndex

        BoardDetailItemModel rowContents = viewModel.getItemsLiveData().getValue();
        String columnTitle = rowContents.getColumnTitles().get(position);
        BoardBaseItemModel cellItemModel = rowContents.getCells().get(position);

        if (holder instanceof BoardDetailStatusItemViewHolder) {
            ((BoardDetailStatusItemViewHolder) holder).setItemModel((BoardStatusItemModel) cellItemModel, columnTitle, position);
        } else if (holder instanceof BoardDetailTextItemViewHolder) {
            ((BoardDetailTextItemViewHolder) holder).setItemModel((BoardTextItemModel) cellItemModel, columnTitle, position);
        } else if (holder instanceof BoardDetailUserItemViewHolder) {
            ((BoardDetailUserItemViewHolder) holder).setItemModel((BoardUserItemModel) cellItemModel, columnTitle, mContext, position);
        } else if (holder instanceof BoardDetailNumberItemViewHolder) {
            ((BoardDetailNumberItemViewHolder) holder).setItemModel((BoardNumberItemModel) cellItemModel, columnTitle, position);
        } else if (holder instanceof BoardDetailCheckboxItemViewHolder) {
            ((BoardDetailCheckboxItemViewHolder) holder).setItemModel((BoardCheckboxItemModel) cellItemModel, columnTitle, position);
        } else if (holder instanceof BoardDetailDateItemViewHolder) {
            ((BoardDetailDateItemViewHolder) holder).setItemModel((BoardDateItemModel) cellItemModel, columnTitle, position);
        } else if (holder instanceof BoardDetailTimelineItemViewHolder) {
            ((BoardDetailTimelineItemViewHolder) holder).setItemModel((BoardTimelineItemModel) cellItemModel, columnTitle, position);
        } else if (holder instanceof BoardDetailUpdateItemViewHolder) {
            ((BoardDetailUpdateItemViewHolder) holder).setItemModel((BoardUpdateItemModel) cellItemModel, columnTitle);
        } else if (holder instanceof BoardDetailMapItemViewHolder) {
            ((BoardDetailMapItemViewHolder) holder).setItemModel((BoardMapItemModel) cellItemModel, columnTitle, position);
        }
    }

    @Override
    public int getItemCount() {
        if (viewModel.getItemsLiveData() != null && viewModel.getItemsLiveData().getValue() != null)
            return viewModel.getItemsLiveData().getValue().getCells().size();
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        String cellType = viewModel.getItemsLiveData().getValue().getCells().get(position).getCellType();
        switch (cellType) {
            case "CellStatus":
                return BoardColumnHeaderModel.ColumnType.Status.getKey();
            case "CellText":
                return BoardColumnHeaderModel.ColumnType.Text.getKey();
            case "CellDate":
                return BoardColumnHeaderModel.ColumnType.Date.getKey();
            case "CellTimeline":
                return BoardColumnHeaderModel.ColumnType.TimeLine.getKey();
            case "CellCheckbox":
                return BoardColumnHeaderModel.ColumnType.Checkbox.getKey();
            case "CellNumber":
                return BoardColumnHeaderModel.ColumnType.Number.getKey();
            case "CellUser":
                return BoardColumnHeaderModel.ColumnType.User.getKey();
            case "CellUpdate":
                return BoardColumnHeaderModel.ColumnType.Update.getKey();
            case "CellMap":
                return BoardColumnHeaderModel.ColumnType.Map.getKey();
            default:
                throw new NotImplementedError();
        }
    }
}
