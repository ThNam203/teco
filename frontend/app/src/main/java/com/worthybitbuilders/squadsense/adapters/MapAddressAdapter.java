package com.worthybitbuilders.squadsense.adapters;

import android.annotation.SuppressLint;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.models.board_models.BoardMapItemModel;

import java.util.List;

public class MapAddressAdapter extends RecyclerView.Adapter<MapAddressAdapter.MapAddressViewHolder> {
    private List<BoardMapItemModel.AddressModel> addressModels = null;
    private ClickHandler clickHandler;
    private boolean isReadOnly = false;

    public MapAddressAdapter() {}

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<BoardMapItemModel.AddressModel> addressModels, boolean isReadOnly, ClickHandler clickHandler) {
        this.addressModels = addressModels;
        this.clickHandler = clickHandler;
        this.isReadOnly = isReadOnly;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MapAddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_address_view, parent, false);
        return new MapAddressViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MapAddressViewHolder holder, int position) {
        holder.bind(addressModels.get(position), position, isReadOnly, clickHandler);
    }

    @Override
    public int getItemCount() {
        if (addressModels == null) return 0;
        else return addressModels.size();
    }

    public static class MapAddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvAddressLocation;
        TextView tvAddressDescription;
        ImageView btnDelete;
        public MapAddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAddressLocation = itemView.findViewById(R.id.tvAddressLocation);
            btnDelete = itemView.findViewById(R.id.btnDeleteAddress);
            tvAddressDescription = itemView.findViewById(R.id.tvAddressDescription);
        }

        public void bind(BoardMapItemModel.AddressModel addressModel, int position, boolean isReadOnly, ClickHandler handler)  {
            this.tvAddressDescription.setText(addressModel.description);
            this.tvAddressLocation.setText(addressModel.title);
            this.itemView.setOnClickListener((view) -> handler.OnAddressClick(addressModel, position));

            if(isReadOnly) btnDelete.setVisibility(View.GONE);
            else btnDelete.setVisibility(View.VISIBLE);
            this.btnDelete.setOnClickListener((view) -> handler.OnDeleteClick(addressModel, position));
        }
    }

    public interface ClickHandler {
        public void OnAddressClick(BoardMapItemModel.AddressModel addressModel, int position);
        public void OnDeleteClick(BoardMapItemModel.AddressModel addressModel, int position);
    }
}
