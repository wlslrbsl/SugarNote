package com.isens.sugarnote;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by jeongjick on 2017-07-13.
 */

public class AlarmAdapter extends BaseAdapter {

    private ArrayList<AlarmItem> mItems = new ArrayList<>();
    private Context context;

    private SharedPreferences prefs_root, prefs_user;
    private SharedPreferences.Editor editor_user;
    private String userAccount;

    private ImageView iv_img;
    private TextView tv_ampm_alarm, tv_hour_alarm, tv_min_alarm, tv_no_alarm;
    private Button btn_alarm_del;

    private TextView[] tv_day;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_alarm, parent, false);
        }

        iv_img = (ImageView) convertView.findViewById(R.id.iv_img);

        tv_ampm_alarm = (TextView) convertView.findViewById(R.id.tv_ampm_alarm);
        tv_hour_alarm = (TextView) convertView.findViewById(R.id.tv_hour_alarm);
        tv_min_alarm = (TextView) convertView.findViewById(R.id.tv_min_alarm);
        tv_no_alarm = (TextView) convertView.findViewById(R.id.tv_no_alarm);

        tv_day = new TextView[7];

        tv_day[0] = (TextView) convertView.findViewById(R.id.tv_sun);
        tv_day[1] = (TextView) convertView.findViewById(R.id.tv_mon);
        tv_day[2] = (TextView) convertView.findViewById(R.id.tv_tue);
        tv_day[3] = (TextView) convertView.findViewById(R.id.tv_wed);
        tv_day[4] = (TextView) convertView.findViewById(R.id.tv_thu);
        tv_day[5] = (TextView) convertView.findViewById(R.id.tv_fri);
        tv_day[6] = (TextView) convertView.findViewById(R.id.tv_sat);

        btn_alarm_del = (Button) convertView.findViewById(R.id.btn_alarm_del);

        final AlarmItem myItem = getItem(position);

        if (myItem.getEnableFlag() == 1)
            iv_img.setImageResource(R.drawable.icon_alarm_set);
        else
            iv_img.setImageResource(R.drawable.icon_alarm_none);

        tv_ampm_alarm.setText(myItem.getAmpm());
        tv_hour_alarm.setText(myItem.getHour());
        tv_min_alarm.setText(myItem.getMinute());
        tv_no_alarm.setText("알람 " + myItem.getAlarm_num());

        myItem.setTv_day(myItem.getDayFlag(), tv_day);

        btn_alarm_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prefs_root = context.getSharedPreferences("ROOT", 0);
                userAccount = prefs_root.getString("SIGNIN", "none");
                prefs_user = context.getSharedPreferences(userAccount, 0);
                editor_user = prefs_user.edit();

                int alarm_cnt = prefs_user.getInt("ALARM#", 0);

                String[] strings = new String[alarm_cnt - 1];

                for (int i = 0; i < alarm_cnt; i++) {
                    if (i < position) {
                        strings[i] = prefs_user.getString("ALARM" + String.valueOf(i + 1), null);
                    } else if (i == position) {

                    } else {
                        char ch = (char) (prefs_user.getString("ALARM" + String.valueOf(i + 1), null).charAt(14) - 1);
                        strings[i - 1] = prefs_user.getString("ALARM" + String.valueOf(i + 1), null).substring(0, 14) + String.valueOf(ch);
                    }
                }

                for (int i = 0; i < alarm_cnt - 1; i++) {
                    editor_user.putString("ALARM" + String.valueOf(i + 1), strings[i]);
                }
                editor_user.putInt("ALARM#", alarm_cnt - 1);
                editor_user.commit();
                AlarmFragment.setListView();
            }
        });

        iv_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prefs_root = context.getSharedPreferences("ROOT", 0);
                userAccount = prefs_root.getString("SIGNIN", "none");
                prefs_user = context.getSharedPreferences(userAccount, 0);
                editor_user = prefs_user.edit();

                if (mItems.get(position).getEnableFlag() == 1) {
                    String str = prefs_user.getString("ALARM" + String.valueOf(position + 1), null);
                    str = str.substring(0, 13) + "0" + str.charAt(14);
                    editor_user.putString("ALARM" + String.valueOf(position + 1), str);
                    editor_user.commit();
                    AlarmFragment.setListView();
                } else {
                    String str = prefs_user.getString("ALARM" + String.valueOf(position + 1), null);
                    str = str.substring(0, 13) + "1" + str.charAt(14);
                    editor_user.putString("ALARM" + String.valueOf(position + 1), str);
                    editor_user.commit();
                    AlarmFragment.setListView();
                }
            }
        });

        return convertView;
    }

    public void addItem(AlarmItem item) {
        mItems.add(item);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public AlarmItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

