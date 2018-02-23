package com.isens.sugarnote;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.isens.module.bloodglucosemonitor.BgmBootLoader;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitor;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitorCallBack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ArrayList<AlarmItem> mItems;
    private FloatingActionButton floatbtn_back, floatbtn_add;
    static private AlarmAdapter alarmAdapter = new AlarmAdapter();
    static private ListView list_alarm;
    private int alarm_cnt = 0;
    private boolean alarm_onoff = false;
    private boolean[] dayFlag = new boolean[7];
    private int setHour, setMin, now_hour, now_min, now_year, now_month, now_day, dayweek, flag;
    private long now, last_alarmmills;
    private Date date;
    private DateFormat df; // HH=24h, hh=12h
    private String now_time;
    private Calendar alram_set_calendar, calendar;
    private LinearLayout home_btn;
    // BGM CB register

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_alarm);

        Intent intent = getIntent(); //이 액티비티를 부른 인텐트를 받는다.
        boolean isAlarm = intent.getBooleanExtra("Alarm", false);
        if (isAlarm == true) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
            Toast.makeText(getApplicationContext(), "알람발생", Toast.LENGTH_SHORT).show();
        }

        mItems = new ArrayList<AlarmItem>();

        floatbtn_add = (FloatingActionButton) findViewById(R.id.floatbtn_add);
        floatbtn_back = (FloatingActionButton) findViewById(R.id.floatbtn_back);

        list_alarm = (ListView) findViewById(R.id.list_alarm);

        floatbtn_add.setOnClickListener(this);
        floatbtn_back.setOnClickListener(this);

        list_alarm.setOnItemClickListener(this);
        list_alarm.setAdapter(alarmAdapter);

        alarm_cnt = list_alarm.getAdapter().getCount();

        if (alarm_cnt > 0) {
            //Alarm_Setting();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tr = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("alarm");

        if (prev != null) {
            tr.remove(prev);
        }

        /*AlarmDialog dialog = new AlarmDialog();
        dialog.show(tr, "alarm");*/
    }

    public static AlarmAdapter getAlarmAdapter() {
        return alarmAdapter;
    }

    public static ListView getList_alarm() {
        return list_alarm;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatbtn_back:
                finish();
                break;

            case R.id.floatbtn_add:

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction tr = fm.beginTransaction();
                Fragment prev = fm.findFragmentByTag("alarm");

                if (prev != null) {
                    tr.remove(prev);
                }
                break;

        }
    }

    private PendingIntent pendingIntent() {

        Intent i = new Intent(this, AlarmActivity.class);
        i.putExtra("Alarm", true);

        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

        Log.i("JJ", "알람울림");

        return pi;

    }

    /*public void Alarm_Setting() {

        last_alarmmills = Long.MAX_VALUE;

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        for (int i = 0; i < alarm_cnt; i++) {
            mItems.add(alarmAdapter.getItem(i));
        }

        for (int alarm_list_idx = 0; alarm_list_idx < alarm_cnt; alarm_list_idx++) {

            alarm_onoff = mItems.get(alarm_list_idx).getEnableFlag();
            if (alarm_onoff == true) {
                for (int i = 0; i < 7; i++) {
                    dayFlag = mItems.get(alarm_list_idx).getDayFlag();
                }

                String ampm = mItems.get(alarm_list_idx).getAmpm();

                if (ampm.equals("AM"))
                    setHour = Integer.parseInt(mItems.get(alarm_list_idx).getHour());
                else
                    setHour = Integer.parseInt(mItems.get(alarm_list_idx).getHour()) + 12;

                setMin = Integer.parseInt(mItems.get(alarm_list_idx).getMinute());

                now = System.currentTimeMillis();
                date = new Date(now);
                df = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss"); // HH=24h, hh=12h
                now_time = df.format(date);
                now_hour = Integer.parseInt(now_time.substring(11, 13));
                now_min = Integer.parseInt(now_time.substring(14, 16));
                now_year = Integer.parseInt(now_time.substring(0, 4));
                now_month = Integer.parseInt(now_time.substring(5, 7));
                now_day = Integer.parseInt(now_time.substring(8, 10));

                alram_set_calendar = Calendar.getInstance();
                alram_set_calendar.setTime(date);
                calendar = Calendar.getInstance();
                calendar.setTime(date);

                dayweek = calendar.get(calendar.DAY_OF_WEEK) - 1;
                flag = 0;

                Log.i("JJ", now_hour + ":" + now_min);
                for (int i = 0; i < 7; i++) {
                    if (dayFlag[i] == true) {
                        if (i - dayweek == 0) {
                            if (setHour - now_hour == 0 && setMin - now_min > 0) {
                                alram_set_calendar.set(now_year, now_month - 1, now_day, setHour, setMin, 0);
                                flag = 1;
                                break;
                            }
                            if (setHour - now_hour > 0) {
                                alram_set_calendar.set(now_year, now_month - 1, now_day, setHour, setMin, 0);
                                flag = 1;
                                break;
                            }
                        }
                        if (i - dayweek > 0) {
                            alram_set_calendar.set(now_year, now_month - 1, now_day, setHour, setMin, 0);
                            alram_set_calendar.add(Calendar.DATE, i - dayweek);
                            flag = 1;
                            break;
                        }
                    }

                }

                if (flag == 0) {
                    for (int i = 0; i < 7; i++) {
                        if (dayFlag[i] == true) {
                            alram_set_calendar.set(now_year, now_month - 1, now_day, setHour, setMin, 0);
                            alram_set_calendar.add(Calendar.DATE, 7 - (dayweek - i));
                            flag = 0;
                            break;
                        }
                    }
                }

                calendar.setTimeInMillis(System.currentTimeMillis());
                long millsalarm = alram_set_calendar.getTimeInMillis() - calendar.getTimeInMillis();


                if (last_alarmmills > millsalarm) {
                    last_alarmmills = millsalarm;
                }
            }
        }

        if (last_alarmmills != Long.MAX_VALUE) {
            Date t = new Date();
            t.setTime(System.currentTimeMillis() + (last_alarmmills));
            alarmManager.set(AlarmManager.RTC_WAKEUP, t.getTime(), pendingIntent());
            Toast.makeText(this, String.valueOf(last_alarmmills / 1000) + "후에", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "알람설정없음", Toast.LENGTH_SHORT).show();

    }*/

}
