package com.worthybitbuilders.squadsense.adapters;

import android.annotation.SuppressLint;
import android.location.Address;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.worthybitbuilders.squadsense.R;

import java.util.List;

public class MapSearchResultAdapter extends RecyclerView.Adapter<MapSearchResultAdapter.MapSearchResultViewHolder> {
    private List<Address> addresses = null;
    OnMapResultClick handler;

    public MapSearchResultAdapter(OnMapResultClick handler) {
        this.handler = handler;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Address> addresses) {
        this.addresses = addresses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MapSearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_search_result_view, parent, false);
        return new MapSearchResultViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MapSearchResultViewHolder holder, int position) {
        Address address = addresses.get(position);
        holder.itemView.setOnClickListener((view) -> this.handler.onClick(address));
        holder.bind(address.getCountryName(), address.getAddressLine(0));
    }

    @Override
    public int getItemCount() {
        if (addresses == null) return 0;
        else return addresses.size();
    }

    public static class MapSearchResultViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvDescription;
        public MapSearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvMapResultTitle);
            tvDescription = itemView.findViewById(R.id.tvMapResultDescription);
        }

        public void bind(String title, String description) {
            tvTitle.setText(title);
            tvDescription.setText(description);
        }
    }

    public interface OnMapResultClick {
        public void onClick(Address address);
    }
}
