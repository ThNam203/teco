package com.worthybitbuilders.squadsense.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.worthybitbuilders.squadsense.R;

import java.util.List;

public class UpdateAdapter extends ArrayAdapter<String> {
    private Context context;
    private int idLayout;

    public UpdateAdapter(Context context, int idLayout, List<String> listUpdate) {
        super(context, idLayout, listUpdate);
        this.context = context;
        this.idLayout = idLayout;
    }

    @NonNull
    @Override
    public View getView(final int position,@NonNull View convertView,@NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(idLayout, parent, false);
        }
        TextView mess = (TextView) convertView.findViewById(R.id.text_update_mess);

        final String update = getItem(position);
        if (update != null) {
            mess.setText(update);
        }
        return convertView;
    }
}
