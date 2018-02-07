package com.isens.sugarnote;

import android.content.Context;
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

    /* 아이템을 세트로 담기 위한 어레이 */
    private ArrayList<AlarmItem> mItems = new ArrayList<>();
    private Context context;

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
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        context = parent.getContext();

        /* 'listview_alarm' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_alarm, parent, false);
        }

        /* 'listview_alarm'에 정의된 위젯에 대한 참조 획득 */
        final ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_img) ;


        TextView tv_ampm_alarm = (TextView) convertView.findViewById(R.id.tv_ampm_alarm) ;
        TextView tv_hour_alarm = (TextView) convertView.findViewById(R.id.tv_hour_alarm) ;
        TextView tv_min_alarm = (TextView) convertView.findViewById(R.id.tv_min_alarm) ;

        TextView[] tv_day = new TextView[7];

        tv_day[0] = (TextView) convertView.findViewById(R.id.tv_sun);
        tv_day[1] = (TextView) convertView.findViewById(R.id.tv_mon);
        tv_day[2] = (TextView) convertView.findViewById(R.id.tv_tue);
        tv_day[3] = (TextView) convertView.findViewById(R.id.tv_wed);
        tv_day[4] = (TextView) convertView.findViewById(R.id.tv_thu);
        tv_day[5] = (TextView) convertView.findViewById(R.id.tv_fri);
        tv_day[6] = (TextView) convertView.findViewById(R.id.tv_sat);



        Button btn_alarm_del = (Button) convertView.findViewById(R.id.btn_alarm_del);

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        final AlarmItem myItem = getItem(position);

        if (myItem.getEnableFlag() == true)
            iv_img.setImageResource(R.drawable.icon_alarm_set);
        else
            iv_img.setImageResource(R.drawable.icon_alarm_none);

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        //iv_img.setImageResource(R.drawable.icon_alarm_set);
        tv_ampm_alarm.setText(myItem.getAmpm());
        tv_hour_alarm.setText(myItem.getHour());
        tv_min_alarm.setText(myItem.getMinute());

        myItem.setTv_day(myItem.getDayFlag(), tv_day);

        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */

        btn_alarm_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItems.remove(position);
                AlarmActivity.getList_alarm().setAdapter(AlarmActivity.getAlarmAdapter());
            }
        });

        iv_img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(mItems.get(position).getEnableFlag() == true) {
                    iv_img.setImageResource(R.drawable.icon_alarm_none);
                    mItems.get(position).setEnableFlag(false);
                    Toast.makeText(context, "알람이 해제되었습니다.", Toast.LENGTH_SHORT).show();

                } else {
                    iv_img.setImageResource(R.drawable.icon_alarm_set);
                    mItems.get(position).setEnableFlag(true);
                    Toast.makeText(context, "알람이 설정되었습니다.", Toast.LENGTH_SHORT).show();

                }
                return false;
            }
        });

        return convertView;
    }

    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addItem(String ampm, String hour, String minute, boolean[] dayFlag) {
        AlarmItem mItem = new AlarmItem();

        /* MyItem에 아이템을 setting한다. */
        mItem.setHour(hour);
        mItem.setMinute(minute);
        mItem.setAmpm(ampm);

        mItem.setDayFlag(dayFlag);

        /* mItems에 MyItem을 추가한다. */
        mItems.add(mItem);
    }

}

