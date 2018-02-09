package com.isens.sugarnote;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {

    private Context context;

    private String[] dateArray;
    private int whatDate, whatMonth, selMonth, whatYear, selYear;

    public GridViewAdapter(Context context) {
        this.context = context;
    }

    public GridViewAdapter(Context context, int maxDate, int firstDay, int whatDate, int whatMonth, int selMonth, int whatYear, int selYear) {

        dateArray = new String[49];
        this.whatDate = whatDate;
        this.whatMonth = whatMonth;
        this.selMonth = selMonth;
        this.whatYear = whatYear;
        this.selYear = selYear;

        dateArray[0] = "일";
        dateArray[1] = "월";
        dateArray[2] = "화";
        dateArray[3] = "수";
        dateArray[4] = "목";
        dateArray[5] = "금";
        dateArray[6] = "토";

        for (int i = 0; i < 42; i++) {
            if (i < firstDay - 1)
                dateArray[i + 7] = "";
            else if (i >= firstDay + maxDate - 1)
                dateArray[i + 7] = "";
            else
                dateArray[i + 7] = String.valueOf(i - firstDay + 2);
        }

        this.context = context;
    }

    @Override
    public int getCount() {
        return this.dateArray.length;
    }

    @Override
    public Object getItem(int position) {
        return dateArray[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView tv;

        if (convertView == null) {
            tv = new TextView(this.context);
            tv.setLayoutParams(new GridView.LayoutParams(40, 40));

            if (position % 7 == 0)
                tv.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
            else if (position % 7 == 6)
                tv.setTextColor(context.getResources().getColor(android.R.color.holo_blue_bright));
            else
                tv.setTextColor(Color.WHITE);

            if (dateArray[position].equals(String.valueOf(whatDate)) && whatMonth == selMonth && whatYear == selYear)
                tv.setTextColor(context.getResources().getColor(android.R.color.holo_green_light));

            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(20f);

        } else {
            tv = (TextView) convertView;
        }
        tv.setText(dateArray[position]);
        return tv;
    }
}
