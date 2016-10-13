package com.beliautopart.beliautopart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.beliautopart.beliautopart.R;
import com.beliautopart.beliautopart.model.RefModel;

import java.util.ArrayList;

/**
 * Created by Brands Pratama on 7/26/2016.
 */
public class MerkAdapter extends ArrayAdapter<RefModel> {

    private ViewHolder viewHolder;

    private static class ViewHolder {
        private TextView itemView;
    }

    public MerkAdapter(Context context, int textViewResourceId, ArrayList<RefModel> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.listview_merk, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemView = (TextView) convertView.findViewById(R.id.txtNama);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        RefModel item = getItem(position);
        if (item!= null) {
            // My layout has only one TextView
            // do whatever you want with your string and long
            viewHolder.itemView.setText(item.getNama());
        }

        return convertView;
    }
}