package com.isens.sugarnote;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapterSetting extends BaseAdapter {

    private ArrayList<CustomAdapterSettingItem> mItem = new ArrayList<>();
    private CustomAdapterSettingItem settingItem;

    private ImageView iv_img;
    private TextView tv_name;

    private Activity activity;

    CustomAdapterSetting(Activity activity){
        this.activity = activity;
    }

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
            LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_power, parent, false);
        }

        iv_img = (ImageView) convertView.findViewById(R.id.img_title);
        tv_name = (TextView) convertView.findViewById(R.id.txt_title);
        iv_img.setVisibility(View.VISIBLE);
        settingItem = getItem(position);

        if("전원 끄기".equals(settingItem.getName()))
            iv_img.setImageResource(R.drawable.img_power);
        else if("재부팅".equals(settingItem.getName()))
            iv_img.setImageResource(R.drawable.icon_alarm_set);
        else if("로그아웃".equals(settingItem.getName()))
            iv_img.setImageResource(R.drawable.img_power);




        tv_name.setText(settingItem.getName());

        return convertView;
    }

    public void addItem(String name) {
        CustomAdapterSettingItem item = new CustomAdapterSettingItem();

        item.setName(name);

        mItem.add(item);
    }
}
