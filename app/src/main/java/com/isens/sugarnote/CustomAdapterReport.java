package com.isens.sugarnote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jeongjick on 2017-11-24.
 */

public class CustomAdapterReport extends BaseAdapter {

    public ArrayList<CustomAdapterReportItem> mItems = new ArrayList<>();

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public CustomAdapterReportItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();

        TextView date_txt, meal_txt,data_txt;
        ImageView meal_icon;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.timelog_list, parent, false);

        meal_icon = (ImageView) convertView.findViewById(R.id.report_meal_icon);
        date_txt    = (TextView) convertView.findViewById(R.id.report_date_txt);
        meal_txt  = (TextView) convertView.findViewById(R.id.report_meal_txt);
        data_txt  = (TextView) convertView.findViewById(R.id.report_gls_txt);

        final CustomAdapterReportItem reportitem = getItem(position);

        String mealoption="";
        mealoption = reportitem.getMeal();

        if(mealoption.equals("식전"))
            meal_icon.setImageResource(R.drawable.premeal_icon_red);
        else if(mealoption.equals("식후"))
            meal_icon.setImageResource(R.drawable.postmeal_icon_red);
        else if(mealoption.equals("공복"))
            meal_icon.setImageResource(R.drawable.nomeal_icon_red);
        else if(mealoption.equals("취침"))
            meal_icon.setImageResource(R.drawable.sleep_icon_red);

        date_txt.setText(reportitem.getDate());
        data_txt.setText(reportitem.getData());
        meal_txt.setText(reportitem.getMeal());


        return convertView;
    }


    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addItem(String date, String meal, String data) {

        CustomAdapterReportItem tItem = new CustomAdapterReportItem();

            tItem.setDate(date);
            tItem.setData(data);
            tItem.setMeal(meal);

        /* mItems에 MyItem을 추가한다. */
        mItems.add(tItem);
    }

    public void deleteItem(int idx){
        mItems.remove(idx);
    }
    public void editItem(String data, String meal, int idx){
            mItems.get(idx).setData(data);
            mItems.get(idx).setMeal(meal);

    }

}
