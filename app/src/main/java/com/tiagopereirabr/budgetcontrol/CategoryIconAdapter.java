package com.tiagopereirabr.budgetcontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class CategoryIconAdapter extends BaseAdapter {
    private Context mContext;
    private int mCatList[];
    private LayoutInflater mInflator;

    public CategoryIconAdapter(Context context, int[] catIcons) {
        mContext = context;
        mCatList = catIcons;
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mCatList.length;
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
        convertView = mInflator.inflate(R.layout.category_spinner_item,null);
        ImageView pic = (ImageView) convertView.findViewById(R.id.cs_image);
        pic.setImageResource(mCatList[position]);
        return convertView;
    }
}