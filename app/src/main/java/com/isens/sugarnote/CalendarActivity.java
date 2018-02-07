package com.isens.sugarnote;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isens.module.bloodglucosemonitor.BgmBootLoader;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitor;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitorCallBack;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CalendarActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout home_btn, btn_upward_calendar, btn_downward_calendar;
    private TextView title;

    private Date date, caldate, later_caldate, pre_caldate, pick_caldate;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    private String now_time, later_time, pre_time, pick_time, myear, mmonth, mdate;
    private int dayWeek, now_year, now_month, now_date, start_date, end_date, later_date, later_month, start_later_date, pre_date, pre_month, data_cnt;
    private static int index, weekStack = 0;

    private long now;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF41D0F4));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);       // 화면을 landscape(가로) 화면으로 고정하고 싶은 경우

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_calendar);

        home_btn = (LinearLayout) findViewById(R.id.btn_home);
        btn_upward_calendar = (LinearLayout) findViewById(R.id.btn_upward_calendar);
        btn_downward_calendar = (LinearLayout) findViewById(R.id.btn_downward_calendar);

        title = (TextView) findViewById(R.id.title_txt);

        home_btn.setOnClickListener(this);
        btn_downward_calendar.setOnClickListener(this);
        btn_upward_calendar.setOnClickListener(this);

        getDateInit();

        //setToday(dayWeek);
        setDate();
        pickDay();
    }

    void getDateInit() {
        now = System.currentTimeMillis();
        date = new Date(now);
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        dayWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /*void setToday(int dayWeek) {
        for (int j = 0; j < 7; j++) {
            tv_calendar_day[j].setBackgroundResource(R.color.color_bright_gray);
            tv_calendar_date[j].setBackgroundColor(Color.WHITE);
            tv_calendar_date[j].setTextColor(Color.GRAY);
        }
        if (dayWeek == 0)
            tv_calendar_day[dayWeek].setTextColor(Color.RED);
        else if (dayWeek == 6)
            tv_calendar_day[dayWeek].setTextColor(Color.BLUE);
        else
            tv_calendar_day[dayWeek].setTextColor(Color.BLACK);
        tv_calendar_day[dayWeek].setBackgroundColor(Color.WHITE);
        tv_calendar_date[dayWeek].setTextColor(Color.WHITE);
        tv_calendar_date[dayWeek].setBackgroundResource(R.drawable.bg_checked);
    }*/

    public void setDate() {

        calendar.add(Calendar.DATE, (7 * weekStack));
        caldate = calendar.getTime();

        dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        now_time = dateFormat.format(caldate);
        now_year = Integer.parseInt(now_time.substring(0, 4));
        now_month = Integer.parseInt(now_time.substring(5, 7));
        now_date = Integer.parseInt(now_time.substring(8, 10));

        calendar.add(Calendar.DATE, 7);
        later_caldate = calendar.getTime();
        later_time = dateFormat.format(later_caldate);
        later_month = Integer.parseInt(later_time.substring(5, 7));
        later_date = Integer.parseInt(later_time.substring(8, 10));

        calendar.add(Calendar.DATE, -14);
        pre_caldate = calendar.getTime();
        pre_time = dateFormat.format(pre_caldate);
        pre_month = Integer.parseInt(pre_time.substring(5, 7));
        pre_date = Integer.parseInt(pre_time.substring(8, 10));

        if (later_month == pre_month) {
            start_date = now_date - dayWeek;
        } else {
            start_date = pre_date + 7 - dayWeek;
        }

        start_later_date = later_date - dayWeek;
        end_date = start_later_date - 1;


        calendar.setTime(date);
    }

    public void pickDay() {
        calendar.add(Calendar.DATE, 7 * weekStack + index);
        pick_caldate = calendar.getTime();
        pick_time = dateFormat.format(pick_caldate);
        myear = pick_time.substring(0, 4);
        mmonth = pick_time.substring(5, 7);
        mdate = pick_time.substring(8, 10);

        pick_time = myear + "/ " + mmonth + "/ " + mdate;

        title.setText(String.valueOf(myear) + "년 " + String.valueOf(mmonth) + "월");

        calendar.setTime(date);
    }


    @Override
    public void onClick(View v) {
        int i = 0;
        switch (v.getId()) {
            case R.id.btn_home:
                finish();
                break;

            case R.id.btn_upward_calendar:
                weekStack -= 1;
                setDate();
                pickDay();
                break;

            case R.id.btn_downward_calendar:
                weekStack += 1;
                setDate();
                pickDay();
                break;

            case R.id.tv_calendar_date_6:
            case R.id.tv_calendar_sat:
                i++;
            case R.id.tv_calendar_date_5:
            case R.id.tv_calendar_fri:
                i++;
            case R.id.tv_calendar_date_4:
            case R.id.tv_calendar_thu:
                i++;
            case R.id.tv_calendar_date_3:
            case R.id.tv_calendar_wed:
                i++;
            case R.id.tv_calendar_date_2:
            case R.id.tv_calendar_tue:
                i++;
            case R.id.tv_calendar_date_1:
            case R.id.tv_calendar_mon:
                i++;
            case R.id.tv_calendar_date_0:
            case R.id.tv_calendar_sun:
                i++;

                index = i - 1 - dayWeek;
                setDate();
                pickDay();
                break;

            default:
                break;
        }
    }

}
