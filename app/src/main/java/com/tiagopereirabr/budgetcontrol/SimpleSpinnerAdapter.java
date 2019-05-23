package com.tiagopereirabr.budgetcontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

public class SimpleSpinnerAdapter extends BaseAdapter {
    private Context mContext;
    private String mList[];
    private LayoutInflater mInflator;

    public SimpleSpinnerAdapter(Context context, String[] list) {
        mContext = context;
        mList = list;
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflator.inflate(R.layout.selection_spinner_item,null);
        TextView text = (TextView) convertView.findViewById(R.id.sel_sp_text);
        text.setText(mList[position]);
        return convertView;
    }
}