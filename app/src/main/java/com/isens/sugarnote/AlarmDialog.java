package com.isens.sugarnote;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;;
import android.widget.ToggleButton;


/**
 * Created by BSPL on 2017-07-17.
 */

public class AlarmDialog extends Dialog implements CompoundButton.OnCheckedChangeListener, TimePicker.OnTimeChangedListener {

    private TextView tv_alarm_monitor;
    private ToggleButton tgb_sun, tgb_mon, tgb_tue, tgb_wed, tgb_thu, tgb_fri, tgb_sat;
    private TimePicker tp_alarm;
    private int[] dayFlag = new int[7];
    private String[] day = {"일", "월", "화", "수", "목", "금", "토"};
    private String ampm, min, hour;
    private int setHour, setMin;

    private Time time;

    public AlarmDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_alarm);

        tp_alarm = (TimePicker) findViewById(R.id.tp_alarm);
        tp_alarm.setOnTimeChangedListener(this);

        tv_alarm_monitor = (TextView) findViewById(R.id.tv_alarm_monitor);

        tgb_sun = (ToggleButton) findViewById(R.id.tgb_sun);
        tgb_mon = (ToggleButton) findViewById(R.id.tgb_mon);
        tgb_tue = (ToggleButton) findViewById(R.id.tgb_tue);
        tgb_wed = (ToggleButton) findViewById(R.id.tgb_wed);
        tgb_thu = (ToggleButton) findViewById(R.id.tgb_thu);
        tgb_fri = (ToggleButton) findViewById(R.id.tgb_fri);
        tgb_sat = (ToggleButton) findViewById(R.id.tgb_sat);

        tgb_sun.setOnCheckedChangeListener(this);
        tgb_mon.setOnCheckedChangeListener(this);
        tgb_tue.setOnCheckedChangeListener(this);
        tgb_wed.setOnCheckedChangeListener(this);
        tgb_thu.setOnCheckedChangeListener(this);
        tgb_fri.setOnCheckedChangeListener(this);
        tgb_sat.setOnCheckedChangeListener(this);


        updateMonitor();

    }

    public void updateMonitor() {
        String str = "매주 ";
        int cnt = 0;
        setHour = tp_alarm.getCurrentHour();
        setMin = tp_alarm.getCurrentMinute();

        if (setHour >= 12) {
            ampm = "PM";
            if (setHour == 12) {
                hour = "12";
            } else {
                if ((setHour % 12) < 10) {
                    hour = "0" + setHour % 12;
                } else {
                    hour = String.valueOf(setHour % 12);
                }
            }
        } else {
            ampm = "AM";
            if (setHour == 0) {
                hour = "12";
            } else {
                if (setHour < 10) {
                    hour = "0" + setHour;
                } else {
                    hour = String.valueOf(setHour);
                }
            }
        }

        if (setMin < 10) {
            min = "0" + setMin;
        } else {
            min = String.valueOf(setMin);
        }

        for (int i = 0; i < 7; i++) {
            if (dayFlag[i]==1) {
                if (cnt != 0)
                    str += ", ";
                str += day[i];
                cnt++;
            }
        }
        if (cnt == 7) {
            str = "매일 " + ampm + " " + hour + ":" + min + " 에 알람이 울립니다";
            tv_alarm_monitor.setText(str);
        } else if (cnt == 0) {

            time = new Time();

            if (time.getHour_now() * 100 + time.getMin_now() < setHour * 100 + setMin)
                str = "오늘 " + ampm + " " + hour + ":" + min + " 에 알람이 울립니다";
            else
                str = "내일 " + ampm + " " + hour + ":" + min + " 에 알람이 울립니다";

            tv_alarm_monitor.setText(str);
        } else {
            tv_alarm_monitor.setText(str + "요일\n" + ampm + " " + hour + ":" + min + " 에 알람이 울립니다");
        }
    }

    public AlarmItem getmItem() {

        AlarmItem alarmItem = new AlarmItem();
        alarmItem.setAmpm(ampm);
        alarmItem.setDayFlag(dayFlag);
        alarmItem.setEnableFlag(1);
        alarmItem.setMinute(String.valueOf(min));
        alarmItem.setHour(String.valueOf(hour));

        return alarmItem;
    }

    public int[] getDayFlag() {
        return this.getDayFlag();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {

            case R.id.tgb_sun:
                if (tgb_sun.isChecked()) {
                    tgb_sun.setTextColor(Color.RED);
                    dayFlag[0] = 1;
                } else {
                    tgb_sun.setTextColor(Color.WHITE);
                    dayFlag[0] = 0;
                }
                updateMonitor();
                break;

            case R.id.tgb_mon:
                if (tgb_mon.isChecked()) {
                    tgb_mon.setTextColor(Color.BLACK);
                    dayFlag[1] = 1;
                } else {
                    tgb_mon.setTextColor(Color.WHITE);
                    dayFlag[1] = 0;
                }
                updateMonitor();
                break;

            case R.id.tgb_tue:
                if (tgb_tue.isChecked()) {
                    tgb_tue.setTextColor(Color.BLACK);
                    dayFlag[2] = 1;
                } else {
                    tgb_tue.setTextColor(Color.WHITE);
                    dayFlag[2] = 0;
                }
                updateMonitor();
                break;

            case R.id.tgb_wed:
                if (tgb_wed.isChecked()) {
                    tgb_wed.setTextColor(Color.BLACK);
                    dayFlag[3] = 1;
                } else {
                    tgb_wed.setTextColor(Color.WHITE);
                    dayFlag[3] = 0;
                }
                updateMonitor();
                break;

            case R.id.tgb_thu:
                if (tgb_thu.isChecked()) {
                    tgb_thu.setTextColor(Color.BLACK);
                    dayFlag[4] = 1;
                } else {
                    tgb_thu.setTextColor(Color.WHITE);
                    dayFlag[4] = 0;
                }
                updateMonitor();
                break;

            case R.id.tgb_fri:
                if (tgb_fri.isChecked()) {
                    tgb_fri.setTextColor(Color.BLACK);
                    dayFlag[5] = 1;
                } else {
                    tgb_fri.setTextColor(Color.WHITE);
                    dayFlag[5] = 0;
                }
                updateMonitor();
                break;

            case R.id.tgb_sat:
                if (tgb_sat.isChecked()) {
                    tgb_sat.setTextColor(Color.BLUE);
                    dayFlag[6] = 1;
                } else {
                    tgb_sat.setTextColor(Color.WHITE);
                    dayFlag[6] = 0;
                }
                updateMonitor();
                break;
        }
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        updateMonitor();
    }
}
