package com.isens.sugarnote;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Created by BSPL on 2017-07-17.
 */

public class AlarmDialog extends Dialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TimePicker.OnTimeChangedListener {

    private TextView  tv_alarm_monitor;
    private ToggleButton tgb_sun, tgb_mon, tgb_tue, tgb_wed, tgb_thu, tgb_fri, tgb_sat;
    private TimePicker tp_alarm;
    private boolean[] dayFlag = new boolean[7];
    private String[] day = {"일", "월", "화", "수", "목", "금", "토"};
    private Context mContext;
    private AlarmManager alarmManager;
    private int setHour, setMin;

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

        mContext = this.getContext();

        updateMonitor();

        alarmManager = (AlarmManager) mContext.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    }

    public void updateMonitor() {
        String str = "매주 ";
        int cnt = 0;
        setHour = tp_alarm.getCurrentHour();
        setMin = tp_alarm.getCurrentMinute();

        for (int i = 0; i < 7; i++) {
            if (dayFlag[i]) {
                if (cnt != 0)
                    str += ", ";
                str += day[i];
                cnt++;
            }
        }
        if (cnt == 7) {
            str = "매일" + setHour + ":" + setMin + " 에 알람이 울립니다";
            tv_alarm_monitor.setText(str);
        } else if (cnt == 0) {
            str = "오늘 or 내일" + setHour + ":" + setMin + " 에 알람이 울립니다";
            tv_alarm_monitor.setText(str);
        } else {
            tv_alarm_monitor.setText(str + "요일\n" + setHour + ":" + setMin + " 에 알람이 울립니다");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void setDialog(int hour, int min) {
        tp_alarm.setCurrentHour(hour);
        tp_alarm.setCurrentMinute(min);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {

            case R.id.tgb_sun:
                if (tgb_sun.isChecked()) {
                    tgb_sun.setTextColor(Color.RED);
                    dayFlag[0] = true;
                } else {
                    tgb_sun.setTextColor(Color.WHITE);
                    dayFlag[0] = false;
                }
                updateMonitor();
                break;

            case R.id.tgb_mon:
                if (tgb_mon.isChecked()) {
                    tgb_mon.setTextColor(Color.BLACK);
                    dayFlag[1] = true;
                } else {
                    tgb_mon.setTextColor(Color.WHITE);
                    dayFlag[1] = false;
                }
                updateMonitor();
                break;

            case R.id.tgb_tue:
                if (tgb_tue.isChecked()) {
                    tgb_tue.setTextColor(Color.BLACK);
                    dayFlag[2] = true;
                } else {
                    tgb_tue.setTextColor(Color.WHITE);
                    dayFlag[2] = false;
                }
                updateMonitor();
                break;

            case R.id.tgb_wed:
                if (tgb_wed.isChecked()) {
                    tgb_wed.setTextColor(Color.BLACK);
                    dayFlag[3] = true;
                } else {
                    tgb_wed.setTextColor(Color.WHITE);
                    dayFlag[3] = false;
                }
                updateMonitor();
                break;

            case R.id.tgb_thu:
                if (tgb_thu.isChecked()) {
                    tgb_thu.setTextColor(Color.BLACK);
                    dayFlag[4] = true;
                } else {
                    tgb_thu.setTextColor(Color.WHITE);
                    dayFlag[4] = false;
                }
                updateMonitor();
                break;

            case R.id.tgb_fri:
                if (tgb_fri.isChecked()) {
                    tgb_fri.setTextColor(Color.BLACK);
                    dayFlag[5] = true;
                } else {
                    tgb_fri.setTextColor(Color.WHITE);
                    dayFlag[5] = false;
                }
                updateMonitor();
                break;

            case R.id.tgb_sat:
                if (tgb_sat.isChecked()) {
                    tgb_sat.setTextColor(Color.BLUE);
                    dayFlag[6] = true;
                } else {
                    tgb_sat.setTextColor(Color.WHITE);
                    dayFlag[6] = false;
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
