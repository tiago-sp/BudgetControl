package com.tiagopereirabr.budgetcontrol;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AvatarAdapter extends BaseAdapter {
    private Context mContext;
    private int mAvatarList[];
    private LayoutInflater mInflator;

    public AvatarAdapter(Context context, int[] avatars) {
        mContext = context;
        mAvatarList = avatars;
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mAvatarList.length;
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
        convertView = mInflator.inflate(R.layout.avatar_spinner_item,null);
        ImageView pic = (ImageView) convertView.findViewById(R.id.cs_image);
        pic.setImageResource(mAvatarList[position]);
        return convertView;
    }
}