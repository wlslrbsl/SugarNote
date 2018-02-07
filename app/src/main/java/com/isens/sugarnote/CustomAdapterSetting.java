package com.isens.sugarnote;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by chong on 2017-12-17.
 */

public class CustomAdapterSetting extends BaseAdapter {

    private ArrayList<CustomAdapterSettingItem> mItem = new ArrayList<>();
    private CustomAdapterSettingItem settingItem;

    private ImageView iv_img;
    private TextView tv_name;

    @Override
    public int getCount() {
        return mItem.size();
    }

    @Override
    public CustomAdapterSettingItem getItem(int position) {
        return mItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_custom, parent, false);
        }

        iv_img = (ImageView) convertView.findViewById(R.id.iv_img);
        tv_name = (TextView) convertView.findViewById(R.id.tv_name);

        settingItem = getItem(position);

        iv_img.setImageDrawable(settingItem.getIcon());
        tv_name.setText(settingItem.getName());

        return convertView;
    }

    public void addItem(Drawable img,String name) {
        CustomAdapterSettingItem item = new CustomAdapterSettingItem();

        item.setIcon(img);
        item.setName(name);

        mItem.add(item);
    }
}
